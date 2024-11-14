// Global variable to keep track of the current category (you can change this dynamically later if needed)
const category = "21";  // Default category

// On page load, fetch the first trivia question
window.onload = function() {
    fetchTrivia();
};

// Function to fetch the trivia question and multiple choices from the backend.
function fetchTrivia() {
    fetch(`http://localhost:8080/getTrivia?category=${category}`)
        .then(response => response.json())
        .then(data => {
            if (data.error) {
                document.getElementById("question").innerText = "Error: " + data.error;
            } else {
                // Display the question
                document.getElementById("question").innerText = data.question;

                // Display the multiple-choice options
                const choicesContainer = document.getElementById("choices");
                choicesContainer.innerHTML = '';  // Clear any previous choices
                data.choices.forEach((choice, index) => {
                    const li = document.createElement("li");
                    li.innerHTML = `<input type="radio" name="answer" value="${choice}"> ${choice}`;
                    choicesContainer.appendChild(li);
                });
            }
        })
        .catch(error => {
            console.error("Error fetching trivia: ", error);
            document.getElementById("question").innerText = "Failed to load trivia. Please try again later.";
        });
}

// Function to submit the user's answer (for now it just shows a result message)
function submitAnswer() {
    const selectedAnswer = document.querySelector('input[name="answer"]:checked');
    const result = document.getElementById("result");

    if (selectedAnswer) {
        result.innerText = `You selected: ${selectedAnswer.value}`;
    } else {
        result.innerText = "Please select an answer!";
    }
}

// Function to load the next question when the "Next" button is clicked
function loadNextQuestion() {
    // Reset the result message
    document.getElementById("result").innerText = "";

    // Fetch the next trivia question
    fetchTrivia();
}
