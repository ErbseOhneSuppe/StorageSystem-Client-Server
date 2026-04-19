import java.time.LocalDateTime;

public class Storage {
    public enum StorageType {
        NORMAL,
        KUEHL,
        GEFAHRSTOFF,
        HOCHREGAL,
        TIEFREGAL,
        SAFE,
    }

    public enum StorageStatus {
        AKTIV,
        GESPERT,
        WARTUNG,
        LEERRAEUMUNG
    }

    private int storageId;
    private String storageName;
    private String location;
    private StorageType type;
    private User manager;
    private LocalDateTime lastUpdate;
    private StorageStatus status;
    private int capacity;

    public Storage(int storageId, String storageName, String location,
                   StorageType type, User manager,
                   LocalDateTime lastUpdate, StorageStatus status,
                   int capacity) {
        this.storageId = storageId;
        this.storageName = storageName;
        this.location = location;
        this.type = type;
        this.manager = manager;
        this.lastUpdate = lastUpdate;
        this.status = status;
        this.capacity = capacity;
    }

    // Getter & Setter

    public int getStorageId() {
        return storageId;
    }

    public void setStorageId(int storageId) {
        this.storageId = storageId;
    }

    public String getStorageName() {
        return storageName;
    }

    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public StorageType getType() {
        return type;
    }

    public void setType(StorageType type) {
        this.type = type;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public StorageStatus getStatus() {
        return status;
    }

    public void setStatus(StorageStatus status) {
        this.status = status;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    // Alles ausgeben als String
    @Override
    public String toString() {
        return "Storage{" +
                "storageId=" + storageId +
                ", storageName='" + storageName + '\'' +
                ", location='" + location + '\'' +
                ", type=" + type +
                ", manager=" + manager +
                ", lastUpdate=" + lastUpdate +
                ", status=" + status +
                ", capacity=" + capacity +
                ", items=" + items.size() +
                '}';
    }
}
