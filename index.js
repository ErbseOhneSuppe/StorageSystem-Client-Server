
function login() {

    let username = document.getElementById("username").value;
    let password = document.getElementById("password").value;

    fetch("http://localhost:2903/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            username: username,
            password: password
        })
    })
        .then(res => res.text())
        .then(data => {

            console.log("SERVER RESPONSE:", data);

            if (data === "OK") {
                localStorage.setItem("role", data.role);

                window.location.href = "dashboard.html";
            } else {
                alert("Login fehlgeschlagen");
            }

        });
}