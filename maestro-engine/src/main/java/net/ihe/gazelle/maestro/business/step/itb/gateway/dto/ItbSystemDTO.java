package net.ihe.gazelle.maestro.business.step.itb.gateway.dto;

import java.util.Objects;

public class ItbSystemDTO {
    private Long id;
    private String shortName;
    private String fullName;

    public ItbSystemDTO() {
    }

    public ItbSystemDTO(Long id, String shortName, String fullName) {
        this.id = id;
        this.shortName = shortName;
        this.fullName = fullName;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItbSystemDTO systemDTO = (ItbSystemDTO) o;
        return Objects.equals(id, systemDTO.id) &&
                Objects.equals(shortName, systemDTO.shortName) &&
                Objects.equals(fullName, systemDTO.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, shortName, fullName);
    }

    @Override
    public String toString() {
        return "SystemDTO{" +
                "id=" + id +
                ", shortName='" + shortName + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}