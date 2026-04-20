
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
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                alert("Login OK");
            } else {
                alert("Login FAIL");
            }
        });
}