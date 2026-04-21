console.log("dashboard.js geladen");

// Beim laden der Seite alles verstecken was eine bestimmte rolle benötigt
window.onload = function() {

    let role = localStorage.getItem("role");

    console.log("ROLE:", role);

    // 🔥 User Button verstecken wenn nicht ADMIN
    if (role !== "ADMIN") {
        //document.getElementById("userBtn").style.display = "none";
    }
};

// Aus der Sidebar die einzelnen Seiten erstellen / anzeigen
function loadPage(page) {

    console.log("Button wurd gedrückt: ", page)

    let content = document.getElementById("content");

    if (page === "storage") {
        content.innerHTML = `
            <h2>Storage Management</h2>
    
            <!-- CREATE -->
            <div>
                <input id="storageName" placeholder="Storage Name">
                <input id="location" placeholder="Location">
    
                <select id="type">
                    <option>NORMAL</option>
                    <option>KUEHL</option>
                    <option>GEFAHRSTOFF</option>
                    <option>HOCHREGAL</option>
                    <option>TIEFREGAL</option>
                    <option>SAFE</option>
                </select>
    
                <input id="managerId" type="number" placeholder="Manager ID">
                <input id="capacity" type="number" placeholder="Capacity">
    
                <button onclick="createStorage()">Create Storage</button>
            </div>
    
            <hr>
    
            <!-- LIST -->
            <div id="storageList"></div>
        `;

        loadStorages();
    }

    if (page === "items") {

        content.innerHTML = `
        <h2>Item Management</h2>

        <!-- CREATE -->
        <div>
            <input id="itemName" placeholder="Item Name">
            <input id="quantity" type="number" placeholder="Quantity">
            <input id="storageId" type="number" placeholder="Storage ID">

            <input id="buyPrice" type="number" placeholder="Buy Price">
            <input id="sellPrice" type="number" placeholder="Sell Price">
            <input id="weight" type="number" placeholder="Weight">

            <button onclick="createItem()">Create Item</button>
        </div>

        <hr>

        <div id="itemList"></div>
    `;

        loadItems();
    }

    if (page === "users") {
        content.innerHTML = `
        <h2>User Management</h2>

        <!-- CREATE -->
        <div>
            <input id="firstName" placeholder="First Name">
            <input id="lastName" placeholder="Last Name">

            <select id="role">
                <option>ADMIN</option>
                <option>MANAGER</option>
                <option>EMPLOYEE</option>
                <option>VISITOR</option>
            </select>

            <input id="password" placeholder="Password">

            <button onclick="createUser()">Create User</button>
        </div>

        <hr>

        <!-- LISTE -->
        <div id="userList"></div>
    `;

        loadUsers();
    }
}

// Nutzer erstellen
function createUser() {

    fetch("/user/create", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            firstName: document.getElementById("firstName").value,
            lastName: document.getElementById("lastName").value,
            role: document.getElementById("role").value,
            password: document.getElementById("password").value
        })
    })
        .then(res => res.text())
        .then(data => {

            console.log("User erstellt:", data);

            document.getElementById("firstName").value = "";
            document.getElementById("lastName").value = "";
            document.getElementById("password").value = "";

            loadUsers();
        });
}

// Nutzer laden
function loadUsers() {

    fetch("/users")
        .then(res => res.json())
        .then(data => {

            let html = `<table border="1" width="100%">
                <tr>
                    <th>ID</th>
                    <th>First Name</th>
                    <th>Last Name</th>
                    <th>Role</th>
                    <th>Action</th>
                </tr>`;

            data.forEach(u => {
                html += `
                    <tr>
                        <td>${u.userId}</td>
                        <td>${u.firstName}</td>
                        <td>${u.lastName}</td>
                        <td>${u.role}</td>
                        <td>
                            <button onclick="deleteUser(${u.userId})">Delete</button>
                        </td>
                    </tr>
                `;
            });

            html += "</table>";

            document.getElementById("userList").innerHTML = html;
        });
}

// Nutzer löschen
function deleteUser(id) {
    fetch("/user/delete?id=" + id, {
        method: "DELETE"
    })
        .then(res => res.text())
        .then(data => {
            console.log(data);
            loadUsers(); // refresh
        });
}

// Storage erstellen
function createStorage() {

    fetch("/storage/create", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            storageName: document.getElementById("storageName").value,
            location: document.getElementById("location").value,
            type: document.getElementById("type").value,
            managerId: parseInt(document.getElementById("managerId").value),
            capacity: parseInt(document.getElementById("capacity").value)
        })
    })
        .then(res => res.text())
        .then(data => {
            console.log("Storage:", data);

            loadStorages();
        });
}

// Storage laden
function loadStorages() {

    fetch("/storages")
        .then(res => res.json())
        .then(data => {

            let html = `<table border="1" width="100%">
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Location</th>
                    <th>Capacity</th>
                    <th>Action</th>
                </tr>`;

            data.forEach(s => {
                html += `
                    <tr>
                        <td>${s.storageId}</td>
                        <td>${s.name}</td>
                        <td>${s.location}</td>
                        <td>${s.capacity}</td>
                        <td>
                            <button onclick="deleteStorage(${s.storageId})">Delete</button>
                        </td>
                    </tr>
                `;
            });

            html += "</table>";

            document.getElementById("storageList").innerHTML = html;
        });
}

// Storage löschen
function deleteStorage(id) {
    fetch("/storage/delete?id=" + id, {
        method: "DELETE"
    })
        .then(res => res.text())
        .then(() => loadStorages());
}

// Item erstellen
function createItem() {

    fetch("/item/create", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            itemName: document.getElementById("itemName").value,
            quantity: parseInt(document.getElementById("quantity").value),
            storageId: parseInt(document.getElementById("storageId").value),
            buyPrice: parseFloat(document.getElementById("buyPrice").value),
            sellPrice: parseFloat(document.getElementById("sellPrice").value),
            weight: parseFloat(document.getElementById("weight").value)
        })
    })
        .then(res => res.text())
        .then(data => {
            console.log("Response:", data);

            loadItems();
        });
}

// Alle Items laden
function loadItems() {

    fetch("/items")
        .then(res => res.json())
        .then(data => {

            let html = `<table border="1" width="100%">
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Qty</th>
                    <th>Storage</th>
                    <th>Action</th>
                </tr>`;

            data.forEach(i => {
                html += `
                    <tr>
                        <td>${i.itemId}</td>
                        <td>${i.itemName}</td>
                        <td>${i.quantity}</td>
                        <td>${i.storageId}</td>
                        <td>
                            <button onclick="deleteItem(${i.itemId})">Delete</button>
                        </td>
                    </tr>
                `;
            });

            html += "</table>";

            document.getElementById("itemList").innerHTML = html;
        });
}

// Item löschen
function deleteItem(id) {
    fetch("/item/delete?id=" + id, {
        method: "DELETE"
    })
        .then(res => res.text())
        .then(() => loadItems());
}