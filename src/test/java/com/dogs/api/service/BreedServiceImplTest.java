package com.dogs.api.service;

import com.dogs.api.dto.request.AddSubBreedRequest;
import com.dogs.api.dto.request.CreateBreedRequest;
import com.dogs.api.dto.request.UpdateBreedRequest;
import com.dogs.api.dto.response.BreedResponse;
import com.dogs.api.exception.BreedAlreadyExistsException;
import com.dogs.api.exception.BreedNotFoundException;
import com.dogs.api.exception.SubBreedAlreadyExistsException;
import com.dogs.api.mapper.BreedMapper;
import com.dogs.api.model.Breed;
import com.dogs.api.model.SubBreed;
import com.dogs.api.repository.BreedRepository;
import com.dogs.api.service.impl.BreedServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BreedServiceImpl Unit Tests")
@DisplayNameGeneration(ReplaceUnderscores.class)
class BreedServiceImplTest {

    @Mock
    private BreedRepository breedRepository;

    private BreedServiceImpl breedService;

    @BeforeEach
    void setUp() {
        breedService = new BreedServiceImpl(breedRepository, new BreedMapper());
    }

    private Breed buildBreed(String name, String... subBreedNames) {
        Breed breed = Breed.builder().id(1L).name(name).build();
        List<SubBreed> subBreeds = new ArrayList<>();
        for (String s : subBreedNames) {
            subBreeds.add(SubBreed.builder().id((long) subBreeds.size() + 1).name(s).breed(breed).build());
        }
        breed.setSubBreeds(subBreeds);
        return breed;
    }

    @Nested
    @DisplayName("getAllBreeds")
    class GetAllBreeds {

    @Test
    @DisplayName("returns all breeds sorted with sub-breeds")
    void getAllBreeds_returnsAllBreeds() {
        List<Breed> breeds = List.of(buildBreed("bulldog", "french", "boston"), buildBreed("poodle"));
        when(breedRepository.findAllWithSubBreeds()).thenReturn(breeds);

        List<BreedResponse> result = breedService.getAllBreeds();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("bulldog");
        assertThat(result.get(0).getSubBreeds()).containsExactly("boston", "french");
    }

    } // end GetAllBreeds

    @Nested
    @DisplayName("getBreed")
    class GetBreed {

    @Test
    @DisplayName("returns breed when it exists")
    void getBreed_existingBreed_returnsBreed() {
        Breed breed = buildBreed("poodle", "miniature", "toy");
        when(breedRepository.findByNameIgnoreCase("poodle")).thenReturn(Optional.of(breed));

        BreedResponse result = breedService.getBreed("poodle");

        assertThat(result.getName()).isEqualTo("poodle");
        assertThat(result.getSubBreeds()).containsExactlyInAnyOrder("miniature", "toy");
    }

    @Test
    @DisplayName("getBreed throws BreedNotFoundException for unknown breed")
    void getBreed_unknownBreed_throwsNotFoundException() {
        when(breedRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> breedService.getBreed("unknown"))
                .isInstanceOf(BreedNotFoundException.class)
                .hasMessageContaining("unknown");
    }

    } // end GetBreed

    @Nested
    @DisplayName("createBreed")
    class CreateBreed {

    @Test
    @DisplayName("saves and returns new breed")
    void createBreed_validRequest_savesBreed() {
        CreateBreedRequest request = new CreateBreedRequest("Labradoodle", List.of("mini"));
        Breed saved = buildBreed("labradoodle", "mini");
        when(breedRepository.existsByNameIgnoreCase("labradoodle")).thenReturn(false);
        when(breedRepository.save(any(Breed.class))).thenReturn(saved);

        BreedResponse result = breedService.createBreed(request);

        assertThat(result.getName()).isEqualTo("labradoodle");
        verify(breedRepository).save(any(Breed.class));
    }

    @Test
    @DisplayName("createBreed throws BreedAlreadyExistsException for duplicate")
    void createBreed_duplicateBreed_throwsAlreadyExistsException() {
        when(breedRepository.existsByNameIgnoreCase("bulldog")).thenReturn(true);

        assertThatThrownBy(() -> breedService.createBreed(new CreateBreedRequest("Bulldog", List.of())))
                .isInstanceOf(BreedAlreadyExistsException.class)
                .hasMessageContaining("bulldog");
    }

    } // end CreateBreed

    @Nested
    @DisplayName("updateBreed")
    class UpdateBreed {

    @Test
    @DisplayName("renames breed successfully")
    void updateBreed_validRequest_renamesBreed() {
        Breed breed = buildBreed("bulldog");
        Breed updated = buildBreed("newbulldog");
        when(breedRepository.findByNameIgnoreCase("bulldog")).thenReturn(Optional.of(breed));
        when(breedRepository.existsByNameIgnoreCase("newbulldog")).thenReturn(false);
        when(breedRepository.save(any(Breed.class))).thenReturn(updated);

        BreedResponse result = breedService.updateBreed("bulldog", new UpdateBreedRequest("newbulldog"));

        assertThat(result.getName()).isEqualTo("newbulldog");
        verify(breedRepository).save(breed);
    }

    } // end UpdateBreed

    @Nested
    @DisplayName("deleteBreed")
    class DeleteBreed {

    @Test
    @DisplayName("removes the breed")
    void deleteBreed_existingBreed_deletesBreed() {
        Breed breed = buildBreed("pug");
        when(breedRepository.findByNameIgnoreCase("pug")).thenReturn(Optional.of(breed));

        breedService.deleteBreed("pug");

        verify(breedRepository).delete(breed);
    }

    @Test
    @DisplayName("deleteBreed throws BreedNotFoundException for unknown breed")
    void deleteBreed_unknownBreed_throwsNotFoundException() {
        when(breedRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> breedService.deleteBreed("ghost"))
                .isInstanceOf(BreedNotFoundException.class);
    }

    } // end DeleteBreed

    @Nested
    @DisplayName("sub-breed operations")
    class SubBreedOperations {

    @Test
    @DisplayName("addSubBreed adds new sub-breed to breed")
    void addSubBreed_validRequest_addsSubBreed() {
        Breed breed = buildBreed("bulldog", "french");
        Breed saved = buildBreed("bulldog", "french", "english");
        when(breedRepository.findByNameIgnoreCase("bulldog")).thenReturn(Optional.of(breed));
        when(breedRepository.save(any(Breed.class))).thenReturn(saved);

        BreedResponse result = breedService.addSubBreed("bulldog", new AddSubBreedRequest("english"));

        assertThat(result.getSubBreeds()).contains("english");
    }

    @Test
    @DisplayName("addSubBreed throws SubBreedAlreadyExistsException for duplicate")
    void addSubBreed_duplicateSubBreed_throwsAlreadyExistsException() {
        Breed breed = buildBreed("bulldog", "french");
        when(breedRepository.findByNameIgnoreCase("bulldog")).thenReturn(Optional.of(breed));

        assertThatThrownBy(() -> breedService.addSubBreed("bulldog", new AddSubBreedRequest("french")))
                .isInstanceOf(SubBreedAlreadyExistsException.class)
                .hasMessageContaining("french");
    }

    @Test
    @DisplayName("deleteSubBreed removes sub-breed from breed")
    void deleteSubBreed_existingSubBreed_removesIt() {
        Breed breed = buildBreed("bulldog", "french", "boston");
        Breed saved = buildBreed("bulldog", "boston");
        when(breedRepository.findByNameIgnoreCase("bulldog")).thenReturn(Optional.of(breed));
        when(breedRepository.save(any(Breed.class))).thenReturn(saved);

        BreedResponse result = breedService.deleteSubBreed("bulldog", "french");

        assertThat(result.getSubBreeds()).doesNotContain("french");
    }

    } // end SubBreedOperations
}
