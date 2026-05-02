package stmarys.library.model;

public abstract class LibraryEntity {
    private int id;

    protected LibraryEntity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
