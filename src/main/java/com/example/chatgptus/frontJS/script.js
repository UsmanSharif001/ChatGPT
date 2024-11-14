document.addEventListener("DOMContentLoaded", function() {
    const questionElem = document.getElementById("question");
    const choicesContainer = document.getElementById("choices-container");
    const submitButton = document.getElementById("submit-btn");
    const resultElem = document.getElementById("result");

    let correctAnswer = ""; // Store the correct answer here

    // Fetch trivia from backend
    function fetchTrivia() {
        console.log("Fetching trivia question from backend...");
        fetch("http://localhost:8080/getTrivia?category=21") // Adjust category if needed
            .then(response => {
                if (!response.ok) {
                    throw new Error("Error fetching trivia");
                }
                return response.json();
            })
            .then(data => {
                console.log("Trivia API Response:", data);

                // Check if the required data exists
                if (!data || !data.question || !data.correct_answer || !Array.isArray(data.answers)) {
                    throw new Error("Trivia data is missing required fields");
                }

                // Store the correct answer
                correctAnswer = data.correct_answer;

                // Display the question
                questionElem.textContent = data.question;

                // Clear previous choices
                choicesContainer.innerHTML = "";

                // Shuffle the answers array to randomize the options
                const shuffledAnswers = data.answers.sort(() => Math.random() - 0.5);

                // Display choices as radio buttons
                shuffledAnswers.forEach((choice, index) => {
                    const choiceElement = document.createElement("div");
                    const radioButton = document.createElement("input");
                    radioButton.type = "radio";
                    radioButton.name = "answer";
                    radioButton.value = choice;
                    radioButton.id = `choice-${index}`;

                    const label = document.createElement("label");
                    label.setAttribute("for", `choice-${index}`);
                    label.textContent = choice;

                    choiceElement.appendChild(radioButton);
                    choiceElement.appendChild(label);
                    choicesContainer.appendChild(choiceElement);
                });

                // Enable the submit button
                submitButton.disabled = false;
            })
            .catch(error => {
                console.error("Error fetching trivia:", error);
                resultElem.textContent = "Error fetching trivia.";
            });
    }

    // Handle answer submission
    submitButton.addEventListener("click", () => {
        const selectedOption = document.querySelector('input[name="answer"]:checked');
        if (selectedOption) {
            const selectedAnswer = selectedOption.value;

            // Check if the answer is correct
            if (selectedAnswer === correctAnswer) {
                resultElem.textContent = "Correct!";
            } else {
                resultElem.textContent = `Incorrect. The correct answer is: ${correctAnswer}`;
            }
        } else {
            resultElem.textContent = "Please select an answer!";
        }

        // Disable the submit button after answering
        submitButton.disabled = true;

        // Fetch a new trivia question after a delay
        setTimeout(() => {
            resultElem.textContent = ""; // Clear previous result
            fetchTrivia();
        }, 3000); // 3-second delay before fetching the next question
    });

    // Fetch the first trivia question when the page loads
    fetchTrivia();
});
