import java.time.LocalDateTime;

public class Item {
    private int itemId;
    private String itemName;
    private int quantity;
    private int storageId;
    private float buyPrice;
    private float sellPrice;
    private float weight;
    private LocalDateTime lastUpdate;

    public Item(int itemId, String itemName, int quantity, int storageId,
                float buyPrice, float sellPrice, float weight,
                LocalDateTime lastUpdate) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.storageId = storageId;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.weight = weight;
        this.lastUpdate = lastUpdate;
    }

    // Getter & Setter

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStorageId() {
        return storageId;
    }

    public void setStorageId(int storageId) {
        this.storageId = storageId;
    }

    public float getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(float buyPrice) {
        this.buyPrice = buyPrice;
    }

    public float getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(float sellPrice) {
        this.sellPrice = sellPrice;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    // Alles ausgeben als String
    @Override
    public String toString() {
        return "Item{" +
                "itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", quantity=" + quantity +
                ", storageId=" + storageId +
                ", buyPrice=" + buyPrice +
                ", sellPrice=" + sellPrice +
                ", weight=" + weight +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
