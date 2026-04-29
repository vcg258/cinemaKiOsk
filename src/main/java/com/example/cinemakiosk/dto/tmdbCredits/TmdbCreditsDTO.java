<<<<<<<< HEAD:src/main/java/com/example/cinemakiosk/dto/Tmdb/TmdbCredits/TmdbCreditsDTO.java
package com.example.cinemakiosk.dto.Tmdb.TmdbCredits;
========
package com.example.cinemakiosk.dto.tmdbCredits;
>>>>>>>> SpringAI:src/main/java/com/example/cinemakiosk/dto/tmdbCredits/TmdbCreditsDTO.java

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TmdbCreditsDTO {
    private List<CastDTO> cast;
    private List<CrewDTO> crew;
}
