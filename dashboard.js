console.log("dashboard.js geladen");

// Beim laden der Seite alles verstecken was eine bestimmte rolle benötigt
window.onload = function() {

    let role = localStorage.getItem("role");

    console.log("ROLE:", role);

    // User Button verstecken wenn nicht ADMIN
    if (role !== "ADMIN") {
        //document.getElementById("userBtn").style.display = "none";
    }

    // Dashboard automatisch laden
    loadPage("dashboard");
};

// Aus der Sidebar die einzelnen Seiten erstellen / anzeigen
function loadPage(page) {

    console.log("Button wurd gedrückt: ", page)

    let content = document.getElementById("content");

    if(page === "dashboard")
    {
        content.innerHTML = `
        <h2>Dashboard</h2>

        <div class="dashboard-grid">

            <div class="card">
                <h3>Lager</h3>
                <span id="storageCount">0</span>
            </div>

            <div class="card">
                <h3>Artikel</h3>
                <span id="itemCount">0</span>
            </div>

            <div class="card">
                <h3>Lagerwert</h3>
                <span id="inventoryValue">0 €</span>
            </div>
            
            <div class="card">
                <h3>Gewinn</h3>
                <span id="profit">0 €</span>
            </div>

        </div>

        <hr>

        <h3>Lagerübersicht</h3>
        <div id="storageOverview"></div>

        <hr>

        <h3>Top Artikel</h3>
        <div id="topItems"></div>
    `;

        loadDashboard();
    }

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
    
                <button class="create-button" onclick="createStorage()">Create Storage</button>
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

            <button class="create-button" onclick="createItem()">Create Item</button>
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

            <button class="create-button" onclick="createUser()">Create User</button>
        </div>

        <hr>

        <!-- LISTE -->
        <div id="userList"></div>
    `;

        loadUsers();
    }

    if(page === "analytics"){

        content.innerHTML = `
    <h2>Analytics</h2>

    <div class="analytics-grid">

        <div class="analytics-card">
            <h3>Artikel pro Lager</h3>
            <canvas id="itemsPerStorageChart"></canvas>
        </div>


        <div class="analytics-card">
            <h3>Lagerwert</h3>
            <canvas id="valuePerStorageChart"></canvas>
        </div>


        <div class="analytics-card">
            <h3>Top Artikel</h3>
            <canvas id="topItemsChart"></canvas>
        </div>


        <div class="analytics-card">
            <h3>Gewinn Top Artikel</h3>
            <canvas id="profitChart"></canvas>
        </div>


        <div class="analytics-card">
            <h3>Kapazität</h3>
            <canvas id="capacityChart"></canvas>
        </div>

    </div>
    `;

        loadAnalytics();
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
            window.editingUserId = null;
            loadUsers();
        });
}

// Nutzer laden
function loadUsers() {

    fetch("/users")
        .then(res => res.json())
        .then(data => {

            let html = `
            <table border="1" width="100%">
                <tr>
                    <th>ID</th>
                    <th>Vorname</th>
                    <th>Nachname</th>
                    <th>Rolle</th>
                    <th>Passwort</th>
                    <th>Aktion</th>
                </tr>
            `;


            data.forEach(u => {

                html += `
                <tr>

                    <td>${u.userId}</td>

                    <td>${u.firstName}</td>

                    <td>${u.lastName}</td>

                    <td>${u.role}</td>

                    <td>
                        <span id="pw-${u.userId}">********</span>
                    </td>

                    <td>
                        <button class="reveal-button" id="btn-${u.userId}" onclick="togglePassword(${u.userId}, '${u.password}')">Anzeigen</button>
                        <button class="delete-button" onclick="deleteUser(${u.userId})">Delete</button>
                    </td>

                </tr>
                `;

            });


            html += "</table>";


            document.getElementById("userList").innerHTML = html;


        })
        .catch(error => {

            console.error("User laden Fehler:", error);

        });

}

// Nutzer löschen
function deleteUser(id) {
    fetch("/user/delete?id=" + id, {
        method: "DELETE"
    })
        .then(res => res.text())
        .then(data => {
            console.log("User gelöscht: ", data);

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
            console.log("Storage erstellt:", data);
            window.editingStorageId = null;
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
                    <th>Standort</th>
                    <th>Typ</th>
                    <th>Status</th>
                    <th>Kapazität</th>
                    <th>Aktion</th>

                </tr>`;


            data.forEach(s => {


                html += `

                    <tr>

                        <td>${s.storageId}</td>

                        <td>${s.name}</td>

                        <td>${s.location}</td>

                        <td>${s.type}</td>

                        <td>${s.status}</td>

                        <td>${s.capacity}</td>


                        <td>

                            <button class="delete-button"
                            onclick="deleteStorage(${s.storageId})">
                            Delete
                            </button>

                        </td>


                    </tr>

                `;


            });


            html += "</table>";


            document.getElementById("storageList").innerHTML = html;


        })
        .catch(error => {

            console.error("Storage Fehler:", error);

        });
}

// Storage löschen
function deleteStorage(id) {

    fetch("/storage/hasItems?id=" + id)
        .then(res => res.text())
        .then(result => {

            if (result === "YES") {
                alert("Das Lager enthält noch Gegenstände! Bitte erst diese entfernen!");
                return;
            }

            // ✅ Wenn leer → löschen
            fetch("/storage/delete?id=" + id, {
                method: "DELETE"
            })
                .then(res => res.text())
                .then(data => {
                    console.log("Storage gelöscht: ", data);
                    loadStorages();
                });
        });
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
            console.log("Item erstellt: ", data);
            loadItems();
        });
}

// Alle Items laden
function loadItems() {

    fetch("/items")
        .then(res => res.json())
        .then(data => {

            let html = `
            <table border="1" width="100%">

            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Menge</th>
                <th>Lager ID</th>
                <th>Einkaufspreis</th>
                <th>Verkaufspreis</th>
                <th>Gewinn</th>
                <th>Gewicht</th>
                <th>Aktion</th>
            </tr>
            `;


            data.forEach(i => {


                let buy = Number(i.buyPrice) || 0;
                let sell = Number(i.sellPrice) || 0;
                let qty = Number(i.quantity) || 0;


                let profit = (sell - buy) * qty;



                html += `

                <tr>

                    <td>${i.itemId}</td>

                    <td>${i.itemName}</td>

                    <td>${qty}</td>

                    <td>${i.storageId}</td>

                    <td>${buy.toFixed(2)} €</td>

                    <td>${sell.toFixed(2)} €</td>

                    <td>${profit.toFixed(2)} €</td>

                    <td>${i.weight}</td>


                    <td>
                        <button 
                        class="delete-button"
                        onclick="deleteItem(${i.itemId})">
                        Delete
                        </button>
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
        .then(data => {
            console.log("Item gelöscht: ", data);
            loadItems(); // refresh
        });
}

function loadDashboard() {

    Promise.all([
        fetch("/storages").then(r => r.json()),
        fetch("/items").then(r => r.json())
    ])
        .then(([storages, items]) => {


            console.log("Storages:", storages);
            console.log("Items:", items);


            // Anzahl Lager
            document.getElementById("storageCount").innerText =
                storages.length;



            // Anzahl Artikel
            let totalItems = items.reduce((sum, i) => {

                return sum + (Number(i.quantity) || 0);

            }, 0);


            document.getElementById("itemCount").innerText =
                totalItems;



            // Lagerwert
            let inventoryValue = items.reduce((sum, i) => {


                let price = Number(i.buyPrice) || 0;
                let quantity = Number(i.quantity) || 0;


                return sum + (price * quantity);


            }, 0);



            document.getElementById("inventoryValue").innerText =
                inventoryValue.toFixed(2) + " €";





            // Gewinn
            let profit = items.reduce((sum, i) => {


                let buy = Number(i.buyPrice) || 0;
                let sell = Number(i.sellPrice) || 0;
                let quantity = Number(i.quantity) || 0;


                return sum + ((sell - buy) * quantity);


            }, 0);



            document.getElementById("profit").innerText =
                profit.toFixed(2) + " €";



            renderStorageOverview(storages, items);

            renderTopItems(items);


        })
        .catch(error => {

            console.error("Dashboard Fehler:", error);

        });

}






function renderStorageOverview(storages, items){


    let html = `

    <table border="1" width="100%">

        <tr>
            <th>Lager</th>
            <th>Ort</th>
            <th>Kapazität</th>
            <th>Belegt</th>
            <th>Auslastung</th>
        </tr>

    `;



    storages.forEach(storage => {



        let amount = items
            .filter(item =>
                Number(item.storageId) === Number(storage.storageId)
            )
            .reduce((sum,item)=>{


                return sum + (Number(item.quantity) || 0);


            },0);




        let percent = 0;


        if(storage.capacity > 0){

            percent =
                ((amount / storage.capacity) * 100)
                    .toFixed(1);

        }





        html += `


        <tr>

            <td>${storage.name}</td>

            <td>${storage.location}</td>

            <td>${storage.capacity}</td>

            <td>${amount}</td>

            <td>${percent}%</td>


        </tr>


        `;


    });



    html += "</table>";



    document.getElementById("storageOverview")
        .innerHTML = html;


}







function renderTopItems(items){



    let sorted = [...items].sort(
        (a,b)=>
            (Number(b.quantity)||0) -
            (Number(a.quantity)||0)
    );



    let html = `


    <table border="1" width="100%">


        <tr>

            <th>Artikel</th>

            <th>Menge</th>

            <th>Wert</th>

            <th>Gewinn</th>

        </tr>


    `;



    sorted.slice(0,5).forEach(item=>{


        let quantity = Number(item.quantity) || 0;

        let buy = Number(item.buyPrice) || 0;

        let sell = Number(item.sellPrice) || 0;



        let value = quantity * buy;


        let profit =
            quantity * (sell - buy);




        html += `


        <tr>


            <td>${item.itemName}</td>


            <td>${quantity}</td>


            <td>${value.toFixed(2)} €</td>


            <td>${profit.toFixed(2)} €</td>


        </tr>


        `;



    });



    html += "</table>";



    document.getElementById("topItems")
        .innerHTML = html;


}

function togglePassword(userId, password) {

    let span = document.getElementById("pw-" + userId);
    let button = document.getElementById("btn-" + userId);

    if (span.innerText === "********") {
        span.innerText = password;
        button.innerText = "Verstecken";
    } else {
        span.innerText = "********";
        button.innerText = "Anzeigen";
    }
}

function loadAnalytics(){

    Promise.all([
        fetch("/storages").then(r=>r.json()),
        fetch("/items").then(r=>r.json())
    ])
        .then(([storages,items])=>{

            renderItemsPerStorageChart(storages,items);
            renderValuePerStorageChart(storages,items);
            renderTopItemsChart(items);
            renderProfitChart(items);
            renderCapacityChart(storages,items);

        });
}

function renderQuantityChart(items) {

    let sorted = [...items]
        .sort((a,b) => b.quantity - a.quantity)
        .slice(0,5);

    new Chart(
        document.getElementById("quantityChart"),
        {
            type: "bar",
            data: {
                labels: sorted.map(i => i.itemName),
                datasets: [{
                    label: "Menge",
                    data: sorted.map(i => i.quantity)
                }]
            }
        }
    );
}

function renderStorageChart(items, storages) {

    let labels = [];
    let values = [];

    storages.forEach(storage => {

        let amount = items
            .filter(i =>
                Number(i.storageId) === Number(storage.storageId))
            .reduce((sum,i)=>
                sum + (Number(i.quantity)||0),0);

        labels.push(storage.name);
        values.push(amount);
    });

    new Chart(
        document.getElementById("storageChart"),
        {
            type: "pie",
            data: {
                labels: labels,
                datasets: [{
                    data: values
                }]
            }
        }
    );
}

function renderItemsPerStorageChart(storages, items) {

    const labels = [];
    const data = [];

    storages.forEach(storage => {

        let amount = items
            .filter(i => i.storageId === storage.storageId)
            .reduce((sum, i) => sum + i.quantity, 0);

        labels.push(storage.name);
        data.push(amount);
    });

    new Chart(
        document.getElementById("itemsPerStorageChart"),
        {
            type: "bar",
            data: {
                labels,
                datasets: [{
                    label: "Artikel",
                    data
                }]
            }
        }
    );
}

function renderValuePerStorageChart(storages, items){

    const labels = [];
    const data = [];

    storages.forEach(storage=>{

        let value = items
            .filter(i => i.storageId === storage.storageId)
            .reduce((sum,i)=>
                    sum + (i.quantity * i.buyPrice)
                ,0);

        labels.push(storage.name);
        data.push(value);
    });

    new Chart(
        document.getElementById("valuePerStorageChart"),
        {
            type:"pie",
            data:{
                labels,
                datasets:[{
                    data
                }]
            }
        }
    );
}

function renderTopItemsChart(items){

    let sorted = [...items]
        .sort((a,b)=>b.quantity-a.quantity)
        .slice(0,10);

    new Chart(
        document.getElementById("topItemsChart"),
        {
            type:"bar",
            data:{
                labels: sorted.map(i=>i.itemName),
                datasets:[{
                    label:"Menge",
                    data: sorted.map(i=>i.quantity)
                }]
            }
        }
    );
}

function renderProfitChart(items){

    let sorted = [...items]
        .sort((a,b)=>
            (b.sellPrice-b.buyPrice)*b.quantity -
            (a.sellPrice-a.buyPrice)*a.quantity
        )
        .slice(0,10);

    new Chart(
        document.getElementById("profitChart"),
        {
            type:"bar",
            data:{
                labels: sorted.map(i=>i.itemName),
                datasets:[{
                    label:"Gewinn",
                    data: sorted.map(i=>
                        (i.sellPrice-i.buyPrice)*i.quantity
                    )
                }]
            }
        }
    );
}

function renderCapacityChart(storages, items){

    const labels = [];
    const data = [];

    storages.forEach(storage=>{

        let amount = items
            .filter(i=>i.storageId === storage.storageId)
            .reduce((sum,i)=>sum+i.quantity,0);

        let percent =
            storage.capacity > 0
                ? (amount/storage.capacity)*100
                : 0;

        labels.push(storage.name);
        data.push(percent);
    });

    new Chart(
        document.getElementById("capacityChart"),
        {
            type:"doughnut",
            data:{
                labels,
                datasets:[{
                    data
                }]
            }
        }
    );
}