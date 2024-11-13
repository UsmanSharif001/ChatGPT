async function sendMessage() {
    const message = document.getElementById("userMessage").value;
    const responseDiv = document.getElementById("response");

    try {
        // Send a GET request to the Spring Boot API
        const res = await fetch(`http://localhost:8080/chat?message=${encodeURIComponent(message)}`);

        if (!res.ok) {
            responseDiv.innerHTML = `<p style="color: red;">Error: ${res.statusText}</p>`;
            return;
        }

        // Parse JSON response
        const data = await res.json();

        // Extract the list of choices
        const choices = data.Choices;

        if (choices && choices.length > 0) {
            // Extract the content of the first choice
            const firstChoiceMessage = choices[0]?.message?.content || "No message content";

            responseDiv.innerHTML = `
                <p><strong>Response:</strong> ${firstChoiceMessage}</p>
            `;
        } else {
            responseDiv.innerHTML = `<p>No valid response from API.</p>`;
        }
    } catch (error) {
        console.error("Error:", error);
        responseDiv.innerHTML = `<p style="color: red;">An error occurred: ${error.message}</p>`;
    }
}
