package com.qa.dogs.service;

import com.google.protobuf.Empty;
import com.qa.dogs.data.BreedEntity;
import com.qa.dogs.data.DogEntity;
import com.qa.dogs.data.repository.BreedRepository;
import com.qa.dogs.data.repository.DogRepository;
import com.qa.grpc.dogs.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import static com.google.protobuf.ByteString.copyFromUtf8;
import static java.util.UUID.fromString;

@GrpcService
public class GrpcDogService extends DogsServiceGrpc.DogsServiceImplBase {

    private final DogRepository dogRepository;
    private final BreedRepository breedRepository;

    @Autowired
    public GrpcDogService(DogRepository dogRepository, BreedRepository breedRepository) {
        this.dogRepository = dogRepository;
        this.breedRepository = breedRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public void getAllDogs(Empty request, StreamObserver<ListDogsResponse> responseObserver) {
        ListDogsResponse dogsResponse = ListDogsResponse.newBuilder()
                .addAllDogs(
                        dogRepository.findAll().stream().map(
                                dogEntity -> DogResponse.newBuilder()
                                        .setBreed(dogEntity.getBreed().getBreed())
                                        .setName(dogEntity.getName())
                                        .setPersonalId(dogEntity.getPersonalId())
                                        .setOwner(dogEntity.getOwner())
                                        .setId(copyFromUtf8(dogEntity.getId().toString()))
                                        .build()
                        ).collect(Collectors.toList())).build();
        responseObserver.onNext(dogsResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void createDog(CreateDogRequest request, StreamObserver<DogResponse> responseObserver) {
        BreedEntity breed = breedRepository.findByBreed(request.getBreed()).orElseThrow(
                () -> new RuntimeException("Cannot find the breed by name: " + request.getBreed()));
        DogEntity dogEntity = new DogEntity();
        dogEntity.setName(request.getName());
        dogEntity.setPersonalId(request.getPersonalId());
        dogEntity.setOwner(request.getOwner());
        dogEntity.setBreed(breed);
        dogEntity = dogRepository.save(dogEntity);

        DogResponse dogResponse = DogResponse.newBuilder()
                .setBreed(breed.getBreed())
                .setName(dogEntity.getName())
                .setPersonalId(dogEntity.getPersonalId())
                .setOwner(dogEntity.getOwner())
                .setId(copyFromUtf8(dogEntity.getId().toString()))
                .build();

        responseObserver.onNext(dogResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void updateDog(UpdateDogRequest request, StreamObserver<DogResponse> responseObserver) {
        BreedEntity breed = breedRepository.findByBreed(request.getDogData().getBreed()).orElseThrow(
                () -> new RuntimeException("Cannot find the breed by name: " + request.getDogData().getBreed()));

        DogEntity dogEntity = dogRepository.findById(fromString(request.getId().toStringUtf8())).orElseThrow(
                () -> new RuntimeException("Cannot find the dog by id: " + request.getId())
        );

        dogEntity.setName(request.getDogData().getName());
        dogEntity.setPersonalId(request.getDogData().getPersonalId());
        dogEntity.setOwner(request.getDogData().getOwner());
        dogEntity.setBreed(breed);
        dogEntity = dogRepository.save(dogEntity);

        DogResponse dogResponse = DogResponse.newBuilder()
                .setName(dogEntity.getName())
                .setPersonalId(dogEntity.getPersonalId())
                .setOwner(dogEntity.getOwner())
                .setBreed(dogEntity.getBreed().getBreed())
                .setId(copyFromUtf8(dogEntity.getId().toString()))
                .build();

        responseObserver.onNext(dogResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteDog(GetDogRequest request, StreamObserver<Empty> responseObserver) {
        DogEntity dogEntity = dogRepository.findById(fromString(request.getId().toStringUtf8())).orElseThrow(
                () -> new RuntimeException("Cannot find the dog by id: " + request.getId()));
                dogRepository.delete(dogEntity);
    }


}
