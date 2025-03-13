function getAllGenres() {
    const genresContainer = document.getElementById('genres-table')
    genresContainer.innerHTML = '';
    fetch('/api/genres')
        .then(response => response.json())
        .then(function (genres) {
            genres.forEach(function (genre) {
                genresContainer.innerHTML += `
    <tr>
      <td>${genre.id}</td>
      <td>${genre.name}</td>
    </tr>
                `;
                console.log("Iter");
                console.log(genre)
            })
        });
}