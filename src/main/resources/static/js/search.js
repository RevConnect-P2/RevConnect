document.addEventListener("DOMContentLoaded", function () {

    function initSearch() {

        const searchInput = document.querySelector("#userSearch");
        const resultsBox = document.querySelector("#searchResults");

        if (!searchInput || !resultsBox) {
            console.log("Search elements not found yet");
            return;
        }

        console.log("Search initialized");

        let debounceTimer;

        searchInput.addEventListener("input", function () {

            const keyword = searchInput.value.trim();

            clearTimeout(debounceTimer);

            if (keyword.length < 2) {
                resultsBox.innerHTML = "";
                resultsBox.style.display = "none";
                return;
            }

            debounceTimer = setTimeout(() => {

                fetch("/users/search?keyword=" + encodeURIComponent(keyword))
                    .then(res => res.json())
                    .then(users => {

                        resultsBox.innerHTML = "";

                        if (!users || users.length === 0) {
                            resultsBox.style.display = "none";
                            return;
                        }

                        users.forEach(username => {

                            const item = document.createElement("div");
                            item.className = "search-item";
                            item.textContent = username;

                            item.addEventListener("click", function () {
                                window.location.href = "/profile/" + username;
                            });

                            resultsBox.appendChild(item);

                        });

                        resultsBox.style.display = "block";

                    })
                    .catch(err => console.error("Search error:", err));

            }, 300);

        });

        document.addEventListener("click", function (e) {

            if (!searchInput.contains(e.target) &&
                !resultsBox.contains(e.target)) {

                resultsBox.style.display = "none";
            }

        });

    }

    initSearch();

});