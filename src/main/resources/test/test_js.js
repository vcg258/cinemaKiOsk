const options = {
  method: 'GET',
  headers: {
    accept: 'application/json',
    Authorization: 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJkMTcwNzkzZjE5ZTgzOGE5ODJkNjM5YzI3NzhhYzBhZiIsIm5iZiI6MTc3NDI0NjU0MC4yNTUsInN1YiI6IjY5YzBkYThjYTY3YzY3MGE4MGQzNGE4MyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.nSp7oBHbZdSN4vZHu8yw4u2_68LXUsaPkCrDHUjZi1A'
  }
};

fetch('https://api.themoviedb.org/3/movie/popular?language=en-US&page=1', options)
  .then(res => res.json())
  .then(res => console.log(res))
  .catch(err => console.error(err));