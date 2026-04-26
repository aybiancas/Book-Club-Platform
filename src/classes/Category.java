package classes;

import java.util.Objects;

public class Category {
    private String categoryId, name, description, colorHex;

    public Category (String categoryId, String name, String description, String colorHex) {
        this.categoryId  = categoryId;
        this.name        = name;
        this.description = description;
        this.colorHex    = colorHex;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setName(String name) { // set/change name
        this.name = name;
    }

    public void setDescription(String description) { // set/change description
        this.description = description;
    }

    public void setColorHex(String colorHex) { // set/change color
        this.colorHex = colorHex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        return Objects.equals(categoryId, ((Category) o).categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId);
    }

    @Override
    public String toString() {
        return "[" + name + "]";
    }
}
