document.addEventListener("DOMContentLoaded", () => {
    const actionForm = document.getElementById("actionForm");
    const responseDiv = document.getElementById("response");

    actionForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        const actionMoveName = document.getElementById("actionMoveName").value;
        const speed = document.getElementById("speed").value;

        const actionMove = { actionMoveName, speed: parseInt(speed) };

        try {
            const response = await fetch('/action', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(actionMove)
            });

            const result = await response.json();
            responseDiv.innerHTML = `<p>Action Move Created: ${JSON.stringify(result)}</p>`;
        } catch (error) {
            responseDiv.innerHTML = `<p>Error: ${error}</p>`;
        }
    });
});
