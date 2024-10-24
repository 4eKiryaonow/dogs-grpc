package com.qa.dogs.service;

import com.google.protobuf.Empty;
import com.qa.dogs.data.BreedEntity;
import com.qa.dogs.data.repository.BreedRepository;
import com.qa.dogs.data.repository.DogRepository;
import com.qa.grpc.dogs.Dog;
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
    public void getAllDogs(Empty request, StreamObserver<ListDogsResponse> responseObserver) {
        ListDogsResponse dogsResponse = ListDogsResponse.newBuilder()
                .addAllDogs(
                        dogRepository.findAll().stream().map(
                        dogEntity -> Dog.newBuilder()
                                .setBreed(dogEntity.getBreed().getName())
                                .setName(dogEntity.getName())
                                .setPersonalId(dogEntity.getPersonalId())
                                .setOwner(dogEntity.getOwner())
                                .build()
                ).collect(Collectors.toList())).build();
        responseObserver.onNext(dogsResponse);
        responseObserver.onCompleted();
    }


}
