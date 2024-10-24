create extension if not exists "uuid-ossp";

create table if not exists "breed"
(
    id UUID unique not null default uuid_generate_v1(),
    breed varchar(35) unique not null,
    primary key (id)
);

alter table breed
    owner to postgres;

delete from breed;

INSERT INTO breed (breed) VALUES
        ('Labrador Retriever'),
        ('Golden Retriever'),
        ('Cocker Spaniel'),
        ('English Springer Spaniel'),
        ('Weimaraner'),
        ('Beagle'),
        ('Basset Hound'),
        ('Dachshund'),
        ('Bloodhound'),
        ('Greyhound'),
        ('Boxer'),
        ('Rottweiler'),
        ('Siberian Husky'),
        ('Doberman Pinscher'),
        ('Great Dane'),
        ('American Pit Bull Terrier'),
        ('Jack Russell Terrier'),
        ('Scottish Terrier'),
        ('Bull Terrier'),
        ('West Highland White Terrier'),
        ('Chihuahua'),
        ('Pomeranian'),
        ('Yorkshire Terrier'),
        ('Maltese'),
        ('Pekingese'),
        ('Bulldog'),
        ('Dalmatian'),
        ('Poodle'),
        ('Shiba Inu'),
        ('Boston Terrier'),
        ('German Shepherd'),
        ('Australian Shepherd'),
        ('Border Collie'),
        ('Shetland Sheepdog'),
        ('Corgi (Pembroke and Cardigan)'),
        ('Dogo Argentino'),
        ('American Bully'),
        ('Belgian Malinois'),
        ('Cane Corso'),
        ('Shikoku'),
        ('Labradoodle'),
        ('Goldendoodle'),
        ('Puggle'),
        ('Cockapoo'),
        ('Pomsky');

create table if not exists "dog"
(
    id UUID unique not null default uuid_generate_v1() primary key,
    name varchar(50) not null,
    personal_id varchar(30) unique not null,
    owner varchar(50),
    breed_id uuid not null,
    constraint fk_dog_breed foreign key (breed_id) references "breed" (id)
);