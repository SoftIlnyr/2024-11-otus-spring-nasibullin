function getAllBooks() {
    const booksContainer = document.getElementById('books-ajax')
    booksContainer.innerHTML = '';
    fetch('/api/books')
        .then(response => response.json())
        .then(function (books) {
            books.forEach(function (book) {
                var genresHtml = '';
                for (const genre of book.genres) {
                    genresHtml += `
            <div>
                <p>${genre.name}</p>
            </div>
                    `
                }

                booksContainer.innerHTML += `
        <div id="book_container" class="book">
            <h6>${book.id}</h6>
            <a href="/books/${book.id}"><h2>${book.title}</h2></a>
            <h4>${book.author.fullName}</h4>
            ${genresHtml}
        </div>
                `;
                console.log("Iter");
                console.log(book)
            })
        });
}

function createBook() {
    const bookTitle = document.getElementById("title").value;
    const authorId = document.getElementById("authorId").value;
    const genreIds = Array.from(document.getElementById("genreIds").selectedOptions)
        .map(option => option.value);
    const bookCreateDto = {title: bookTitle, authorId: authorId, genreIds: genreIds}

    fetch("/api/books", {
        method: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(bookCreateDto)
    })
        .then(response => response.json())
        .then(response => {
            console.log("Saved book: " + JSON.stringify(response));
            getAllBooks();
        });
}

function updateBook() {
    const bookId = document.getElementById("bookId").value;
    const bookTitle = document.getElementById("title").value;
    const authorId = document.getElementById("authorId").value;
    const genreIds = Array.from(document.getElementById("genreIds").selectedOptions)
        .map(option => option.value);
    const bookUpdateDto = {id: bookId, title: bookTitle, authorId: authorId, genreIds: genreIds}

    fetch("/api/books/" + bookId, {
        method: "PUT",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(bookUpdateDto)
    })
        .then(response => {
            if (response.ok) {
                console.log("Updated book: " + JSON.stringify(response.json()));
                window.location.href = "/books/" + bookId;
            }
        });
}

function deleteBook() {
    const bookId = document.getElementById("deleteBookId").value;

    fetch("/api/books/" + bookId, {
        method: "DELETE",
        body: {}
    })
        .then(response => {
            if (response.ok) {
                console.log("Deleted book: " + bookId);
                window.location.href = "/books";
            }
        });
}

function createBookComment() {
    const bookId = document.getElementById("bookId").value;
    const commentText = document.getElementById("text").value;
    const commentCreateDto = {bookId: bookId, text: commentText}

    fetch("/api/books/" + bookId + "/comments", {
        method: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(commentCreateDto)
    })
        .then(response => {
            if (response.ok) {
                window.location.href = "/books/" + bookId;
            }
        });

}