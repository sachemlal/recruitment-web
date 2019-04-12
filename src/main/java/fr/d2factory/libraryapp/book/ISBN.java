package fr.d2factory.libraryapp.book;

public class ISBN {
    private long isbnCode;

    public ISBN(long isbnCode) {
        this.isbnCode = isbnCode;
    }

    public long getIsbnCode() {
        return isbnCode;
    }

    @Override
    public boolean equals(Object o) {
        return this.getIsbnCode() == ((ISBN) o).getIsbnCode();
    }

    @Override
    public int hashCode() {
        return (int) isbnCode;
    }
}
