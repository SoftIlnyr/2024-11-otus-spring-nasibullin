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