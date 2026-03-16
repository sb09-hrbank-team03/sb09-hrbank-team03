package com.sb09.hrbank.service.basic;

import com.sb09.hrbank.entity.FileMeta;
import com.sb09.hrbank.repository.FileMetaRepository;
import com.sb09.hrbank.service.FileService;
import com.sb09.hrbank.storage.FileStorage;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BasicFileService implements FileService {

  private static final String PROFILE_IMAGES_DIR = "profile-images";

  private final FileMetaRepository repository;
  private final FileStorage storage;

  @Override
  public Resource download(Long id) {
    FileMeta file = repository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 파일을 찾을 수 없습니다. id=" + id));
    return storage.load(file.getPath());
  }

  @Override
  public void delete(Long id) {
    FileMeta file = repository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 파일을 찾을 수 없습니다. id=" + id));
    try {
      storage.delete(file.getPath());
    } catch (NoSuchElementException e) {
      // 파일이 존재하지 않는 경우에도 메타데이터는 삭제하도록 처리
    }
    repository.deleteById(id);
  }

  @Override
  public FileMeta save(Path path) {

    FileMeta file = new FileMeta(
        path.getFileName().toString(),
        path.toFile().length(),
        "text/csv",
        path.toString()
    );
    return repository.save(file);
  }

  @Override
  public FileMeta saveProfileImage(MultipartFile profileImage) {
    if (profileImage == null || profileImage.isEmpty()) {
      throw new IllegalArgumentException("업로드할 프로필 이미지가 비어 있습니다.");
    }

    String contentType = profileImage.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new IllegalArgumentException("프로필 이미지는 image/* 타입만 업로드할 수 있습니다.");
    }

    Path savedPath = storage.store(profileImage, PROFILE_IMAGES_DIR);
    FileMeta file = new FileMeta(
        savedPath.getFileName().toString(),
        profileImage.getSize(),
        contentType,
        savedPath.toString()
    );
    return repository.save(file);
  }
}