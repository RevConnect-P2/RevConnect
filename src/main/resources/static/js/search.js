console.log("🔥 search.js loaded");

(function () {

    document.addEventListener("DOMContentLoaded", () => {

        const input = document.getElementById("globalSearchInput");
        const resultsBox = document.getElementById("searchResults");

        if (!input || !resultsBox) {
            console.error("❌ Search input or results box not found");
            return;
        }

        let debounceTimer;

        input.addEventListener("input", () => {
            const query = input.value.trim();

            clearTimeout(debounceTimer);

            if (query.length < 2) {
                resultsBox.style.display = "none";
                resultsBox.innerHTML = "";
                return;
            }

            debounceTimer = setTimeout(() => {
                fetch(`/profiles/search?query=${encodeURIComponent(query)}`)
                    .then(res => res.json())
                    .then(data => {
                        resultsBox.innerHTML = "";

                        if (!data.length) {
                            resultsBox.style.display = "none";
                            return;
                        }

                        data.forEach(profile => {
                            const item = document.createElement("a");
                            item.href = `/profile/${profile.userId}`;
                            item.className = "list-group-item list-group-item-action";
                            item.textContent = profile.username;
                            resultsBox.appendChild(item);
                        });

                        resultsBox.style.display = "block";
                    })
                    .catch(err => console.error(err));
            }, 300);
        });

        document.addEventListener("click", (e) => {
            if (!resultsBox.contains(e.target) && e.target !== input) {
                resultsBox.style.display = "none";
            }
        });

    });

})();