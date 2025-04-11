function getAllAuthors() {
    const authorsContainer = document.getElementById('authors-table')
    authorsContainer.innerHTML = '';
    fetch('/api/genres')
        .then(response => response.json())
        .then(function (authors) {
            authors.forEach(function (author) {
                authorsContainer.innerHTML += `
    <tr>
      <td>${author.id}</td>
      <td>${author.name}</td>
    </tr>
                `;
            })
        });
}