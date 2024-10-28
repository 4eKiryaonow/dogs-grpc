package com.qa.dogs.service;

import com.google.protobuf.Empty;
import com.qa.dogs.data.BreedEntity;
import com.qa.dogs.data.DogEntity;
import com.qa.dogs.data.repository.BreedRepository;
import com.qa.dogs.data.repository.DogRepository;
import com.qa.grpc.dogs.CreateDogRequest;
import com.qa.grpc.dogs.DogResponse;
import com.qa.grpc.dogs.DogsServiceGrpc;
import com.qa.grpc.dogs.ListDogsResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

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
                .build();

        responseObserver.onNext(dogResponse);
        responseObserver.onCompleted();
    }
}
