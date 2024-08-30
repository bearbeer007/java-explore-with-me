package ru.practicum.category.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    private Long id;
    @NotNull
    @Size(min = 1, max = 50)
    private String name;
}
