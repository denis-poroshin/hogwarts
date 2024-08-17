package ru.hogwarts.springhogwars.services;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.springhogwars.exceptions.AvatarProcessingException;
import ru.hogwarts.springhogwars.exceptions.NotCorrectValueException;
import ru.hogwarts.springhogwars.exceptions.StudentNotFoundException;
import ru.hogwarts.springhogwars.models.Avatar;
import ru.hogwarts.springhogwars.models.Student;
import ru.hogwarts.springhogwars.repositories.AvatarRepository;
import ru.hogwarts.springhogwars.repositories.StudentRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.UUID;

@Service
public class AvatarService {

    private final Path path;

    private final StudentRepository studentRepository;

    private final AvatarRepository avatarRepository;

    private Logger logger = LoggerFactory.getLogger(AvatarService.class);


    public AvatarService(StudentRepository studentRepository,
                         AvatarRepository avatarRepository,
                         @Value("${student.avatars-dir-path-name}") String avatarDirName) {
        this.studentRepository = studentRepository;
        this.avatarRepository = avatarRepository;
        path = Paths.get(avatarDirName);
    }

    @Transactional
    public void uploadAvatar(MultipartFile multipartFile, long studentId) {
        logger.info("Uploading avatar");
        try {
            byte[] data = multipartFile.getBytes();
            String extension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
            Path avatarPath = path.resolve(UUID.randomUUID() + "." + extension);
            //Files.write(avatarPath, data);
            Files.createDirectories(avatarPath.getParent());
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new StudentNotFoundException(studentId));
            Avatar avatar = avatarRepository.findByStudent_Id(studentId)
                    .orElseGet(Avatar::new);
            avatar.setStudent(student);
            avatar.setData(data);
            avatar.setFileSize(data.length);
            avatar.setMediaType(multipartFile.getContentType());
            avatar.setFilePath(avatarPath.toString());
            avatarRepository.save(avatar);
        } catch (IOException e) {
            throw new AvatarProcessingException();
        }
    }
    @Transactional
    public Pair<byte[], String> getAvatarFromDb(long studentId){
        logger.info("Getting avatar from DB");
        Avatar avatar = avatarRepository.findByStudent_Id(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        return Pair.of(avatar.getData(), avatar.getMediaType());

    }

    @Transactional
    public Pair<byte[], String> getAvatarFromFs(long studentId){
        logger.info("Getting avatar from Fs");
        try {
            Avatar avatar = avatarRepository.findByStudent_Id(studentId)
                    .orElseThrow(() -> new StudentNotFoundException(studentId));
            System.out.println(avatar);
            return Pair.of(Files.readAllBytes(Path.of(avatar.getFilePath())), avatar.getMediaType());
        }catch (IOException e){
            e.printStackTrace();
            throw new AvatarProcessingException();
        }


    }

    public Collection<Avatar> getAllAvatars(Integer pageNumber, Integer pageSize){
        logger.info("Getting all avatars");
        if(pageNumber == 0){
            throw new NotCorrectValueException();
        }
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return avatarRepository.findAll(pageRequest).getContent();
    }

}
