async function sendMessage() {
    const message = document.getElementById("userMessage").value;
    const responseDiv = document.getElementById("response");

    try {
        let res;
        let data;

        if (message.toLowerCase().includes("trivia")) {
            // Fetch trivia question when user mentions "trivia"
            res = await fetch(`http://localhost:8080/sportsTrivia`);
            data = await res.json();

            if (data.question) {
                const question = data.question;
                const answers = data.answers || [];
                const correctAnswer = data.correctAnswer;

                // Display the trivia question and options
                responseDiv.innerHTML = `<p><strong>Trivia Question:</strong> ${question}</p>`;
                answers.forEach((answer, index) => {
                    responseDiv.innerHTML += `<button onclick="checkAnswer('${answer}', '${correctAnswer}')">${answer}</button>`;
                });
            } else {
                responseDiv.innerHTML = `<p>No trivia available. Try again later.</p>`;
            }
        } else {
            // Otherwise, call the regular chat API
            res = await fetch(`http://localhost:8080/chat?message=${encodeURIComponent(message)}`);
            data = await res.json();
            const responseMessage = data.message || "No valid response from the system.";
            responseDiv.innerHTML = `<p><strong>Response:</strong> ${responseMessage}</p>`;
        }
    } catch (error) {
        console.error("Error:", error);
        responseDiv.innerHTML = `<p style="color: red;">An error occurred: ${error.message}</p>`;
    }
}

// Function to check user's answer
function checkAnswer(selectedAnswer, correctAnswer) {
    const responseDiv = document.getElementById("response");
    if (selectedAnswer === correctAnswer) {
        responseDiv.innerHTML += `<p style="color: green;"><strong>Correct!</strong> The answer is ${correctAnswer}.</p>`;
    } else {
        responseDiv.innerHTML += `<p style="color: red;"><strong>Incorrect!</strong> The correct answer was...not that</p>`;
    }
}
