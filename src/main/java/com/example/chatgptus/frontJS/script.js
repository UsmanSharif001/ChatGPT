let currentQuestionIndex = 0;
let questions = [];
let correctAnswer = "";

// Henter spørgsmålene fra vores gratis API
async function fetchQuestions(categoryId = 21) {
    try {
        const response = await fetch(`https://opentdb.com/api.php?amount=10&category=${categoryId}&type=multiple`);
        const data = await response.json();
        questions = data.results;
        loadQuestion(currentQuestionIndex);
    } catch (error) {
        console.error("Error fetching questions:", error);
    }
}

// Funktion til at hente næste spørgsmål og ved færdiggørelse af quizzen
function loadQuestion(index) {

    if (index >= questions.length) {
        alert("Quiz Finished!");
        document.getElementById("trivia-container").innerHTML = "<h2>Managed to reach the end, nerd!</h2>";
        return;
    }

    const questionData = questions[index];
    const questionElement = document.getElementById("question");
    const choicesList = document.getElementById("choices");

    // Viser spørgsmålene
    questionElement.textContent = questionData.question;

    // Sletter dit svar fra choiceListen i tilfælde af det er et forkert svar
    choicesList.innerHTML = "";

    // I tilfælde af af korrekt svar til det bruges til at prompte dig til næste spørgsmål
    correctAnswer = questionData.correct_answer;

    // Combine correct and incorrect answers
    const choices = [...questionData.incorrect_answers, correctAnswer];

    // Sørger for at det aldrig kun er svarmulighed 1 men at det er random
    choices.sort(() => Math.random() - 0.5);

    // Itterer igennem vores choices array og henter svarmulighederne
    choices.forEach((choice, index) => {
        const listItem = document.createElement("li");
        const radioInput = document.createElement("input");
        radioInput.type = "radio";
        radioInput.name = "choice";
        radioInput.value = choice;
        radioInput.id = `choice${index}`;

        const label = document.createElement("label");
        label.htmlFor = `choice${index}`;
        label.textContent = choice;

        listItem.appendChild(radioInput);
        listItem.appendChild(label);
        choicesList.appendChild(listItem);
    });
}

// Her er funktionen som sørger for at ved tryk på en af svarmulighederne bliver den checked og hvis det er korrekt prompter dig videre
function submitAnswer() {
    const selectedChoice = document.querySelector('input[name="choice"]:checked');
    const resultElement = document.getElementById("result");

    if (!selectedChoice) {
        resultElement.textContent = "Please select an answer.";
        return;
    }

    const userAnswer = selectedChoice.value;
    // Hvis korrekt svar hent næste spørgsmål (++) fra vores spørgsmålsindex medmindre det er færdigt - ellers må det være et forkert svar og du skal derfor prøve igen!
    if (userAnswer === correctAnswer) {
        resultElement.textContent = "Correct! ";
        setTimeout(() => {
            resultElement.textContent = "";
            currentQuestionIndex++;
            loadQuestion(currentQuestionIndex);
        }, 1500);
    } else {
        resultElement.textContent = "Incorrect. Try again.";
    }
}

function changeCategory() {
    const categoryId = document.getElementById("categorySelect").value;
    fetchQuestions(categoryId);
}

// Henter spørgsmålene - kategoriID 21 er sportsID - eksempelvis er kategoriID 9 generel viden
fetchQuestions(21);
