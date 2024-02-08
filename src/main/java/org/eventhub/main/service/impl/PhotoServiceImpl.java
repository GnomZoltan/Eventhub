package org.eventhub.main.service.impl;

import org.eventhub.main.exception.NullEntityReferenceException;
import org.eventhub.main.model.Photo;
import org.eventhub.main.repository.PhotoRepository;
import org.eventhub.main.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PhotoServiceImpl implements PhotoService {
    private final PhotoRepository photoRepository;

    @Autowired
    public PhotoServiceImpl(PhotoRepository photoRepository){
        this.photoRepository = photoRepository;
    }

    @Override
    public Photo create(Photo photo) {
        if(photo != null){
            return photoRepository.save(photo);
        }
        throw new NullEntityReferenceException("Created photo can't be null");
    }

    @Override
    public Photo readById(long id) {
        return photoRepository.findById(id)
                .orElseThrow(()->new NullEntityReferenceException("Photo with given id is not found"));
    }

    @Override
    public Photo update(Photo photo) {
        if (photo != null) {
            readById(photo.getId());
            return photoRepository.save(photo);
        }
        throw new NullEntityReferenceException("Updated photo cannot be 'null'");
    }

    @Override
    public void delete(long id) {
        photoRepository.delete(readById(id));
    }

    @Override
    public List<Photo> getAll() {
        return photoRepository.findAll();
    }
}
