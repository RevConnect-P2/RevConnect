// ===============================
// GLOBAL STATE
// ===============================
let currentUser = {
    userId: window.CURRENT_USER_ID || null
};
let productServiceTags = [];
let savedPostIds = new Set();

// ===============================
// FEED FILTER STATE
// ===============================
let allFeedPosts = [];
let currentFeedFilter = "ALL"; // ALL | NORMAL | PROMOTIONAL


// ===============================
// ADD PRODUCT / SERVICE TAG
// ===============================
function addTag() {
    const tagNameInput = document.getElementById("tagName");
    const tagTypeInput = document.getElementById("tagType");
    const tagList = document.getElementById("tagList");

    if (!tagNameInput || !tagTypeInput || !tagList) return;

    const tagName = tagNameInput.value.trim();
    const tagType = tagTypeInput.value;

    if (!tagName) {
        alert("Tag name cannot be empty");
        return;
    }

    productServiceTags.push({ tagName, tagType });

    tagNameInput.value = "";
    renderTags();
}

function renderTags() {
    const tagList = document.getElementById("tagList");
    if (!tagList) return;

    tagList.innerHTML = "";

    productServiceTags.forEach((tag, index) => {
        const badge = document.createElement("span");
        badge.className = "badge bg-secondary me-2 mb-1";
        badge.innerHTML = `
            ${tag.tagName} (${tag.tagType})
            <span style="cursor:pointer;margin-left:6px;"
                  onclick="removeTag(${index})">✕</span>
        `;
        tagList.appendChild(badge);
    });
}

function removeTag(index) {
    productServiceTags.splice(index, 1);
    renderTags();
}

// ===============================
// CREATE NEW POST
// ===============================
function createPost() {
console.log("Create post clicked", currentUser);
    if (!currentUser) return;

    const content = document.getElementById("postContent")?.value.trim();
    const postTypeEl = document.getElementById("postType");
    const postType = postTypeEl ? postTypeEl.value : "NORMAL";
    const hashtagsInput = document.getElementById("hashtags")?.value || "";
    const ctaText = document.getElementById("ctaText")?.value.trim();
    const ctaLink = document.getElementById("ctaLink")?.value.trim();
    const scheduledAt = document.getElementById("scheduledAt")?.value;

    if (!content) {
        alert("Post content cannot be empty");
        return;
    }

    if (postType === "PROMOTIONAL") {
        if (!ctaText || !ctaLink) {
            alert("CTA Text and CTA Link are required for promotional posts");
            return;
        }
    }

    const hashtags = hashtagsInput
        .split(" ")
        .filter(tag => tag.startsWith("#"))
        .map(tag => tag.substring(1));

    const requestBody = {
        content,
        postType,
        hashtags
    };

    if (postType === "PROMOTIONAL") {
        requestBody.ctaText = ctaText;
        requestBody.ctaLink = ctaLink;
        requestBody.tags = productServiceTags;

        if (scheduledAt) {
            requestBody.scheduledAt = scheduledAt;
        }
    }

    fetch(`/posts?userId=${currentUser.userId}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(requestBody)
    })
        .then(response => {
            if (!response.ok) throw new Error("Failed to create post");
            return response.json();
        })
        .then(() => {
            resetCreatePostForm();
            alert("Post created successfully");
            window.location.href = "/dashboard";
        })
        .catch(err => alert(err.message));
}

// ===============================
// RESET FORM
// ===============================
function resetCreatePostForm() {
    ["postContent", "hashtags", "ctaText", "ctaLink", "scheduledAt"].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = "";
    });

    productServiceTags = [];
    renderTags();
}

// ===============================
// FORMAT DATE
// ===============================
function formatDate(dateTime) {
    if (!dateTime) return "";
    return new Date(dateTime).toLocaleString();
}

function createFeedPostCard(post, options = {}) {
    const card = document.createElement("div");
    card.className = "card feed-card mb-3";

    const hashtags = (post.hashtags || [])
        .map(tag => `<span class="text-primary me-2">#${tag}</span>`)
        .join("");

    const tagsHtml = (post.tags || [])
        .map(tag => `
            <span class="badge me-2
             ${
                tag.tagType === 'PRODUCT'
                    ? 'bg-info'
                    : 'bg-success'}">
                ${tag.tagType}: ${tag.tagName}
            </span>
        `)
        .join("");

    card.innerHTML = `
        <div class="card-body">

            <div class="d-flex justify-content-between">
                <strong>${post.username}</strong>
                ${options.showPinned && post.pinned
                    ? `<span class="text-warning">📌 Pinned</span>`
                    : ""}
            </div>

            <p class="post-text mt-2">${post.content}</p>

            ${hashtags ? `<div class="mb-2">${hashtags}</div>` : ""}

            ${tagsHtml ? `<div class="mt-2">${tagsHtml}</div>` : ""}
            ${
                post.postType === "PROMOTIONAL" &&
                post.ctaText &&
                post.ctaLink
                ? `
                <div class="mt-2">
                    <a href="/products/${post.postId}"
                       class="btn btn-sm btn-primary">
                        ${post.ctaText}
                    </a>
                </div>
                `
                : ""
            }

            ${
                post.scheduledAt
                ? `<small class="text-muted">⏰ Scheduled</small>`
                : ""
            }

            <hr>

            <div class="row text-center align-items-center">

                <div class="col post-action">👍 Like</div>
                <div class="col post-action">💬 Comment</div>
                <div class="col post-action">🔗 Share</div>

                <div class="col post-action text-end">
                    <i class="bi ${options.isSaved ? 'bi-bookmark-fill' : 'bi-bookmark'}"
                       style="cursor:pointer;font-size:18px"
                       onclick="
                       ${options.isSaved
                            ? `unsavePost(${post.postId}, this)`
                            : `savePost(${post.postId}, this)`}"
                       title="Save post"></i>
                </div>

            </div>

        </div>
    `;

    return card;
}

// ===============================
// CREATE MY POST CARD
// ===============================
function createMyPostCard(post) {

    const card = document.createElement("div");
    card.className = "card feed-card mb-3";

    card.innerHTML = `
        <div class="card-body">

            <div class="d-flex justify-content-between align-items-start">
                <div>
                    <strong>${post.username}</strong>
                    ${post.pinned ? `<span class="ms-2 text-warning">📌 Pinned</span>` : ""}
                </div>

                <!-- 3 DOT MENU -->
                <div class="dropdown">
                    <button class="btn btn-sm btn-light" data-bs-toggle="dropdown">
                        <i class="bi bi-three-dots"></i>
                    </button>

                    <ul class="dropdown-menu dropdown-menu-end">
                        ${
                            post.pinned
                                ? `<li>
                                       <a class="dropdown-item"
                                          href="#"
                                          onclick="unpinPostAction(${post.postId})">
                                          Unpin
                                       </a>
                                   </li>`
                                : `<li>
                                       <a class="dropdown-item"
                                          href="#"
                                          onclick="pinPostAction(${post.postId})">
                                          Pin
                                       </a>
                                   </li>`
                        }

                        <li>
                            <a class="dropdown-item"
                               href="/posts/edit/${post.postId}">
                               Update
                            </a>
                        </li>

                        <li><hr class="dropdown-divider"></li>

                        <li>
                            <a class="dropdown-item text-danger"
                               href="#"
                               onclick="deletePostAction(${post.postId})">
                               Delete
                            </a>
                        </li>
                    </ul>
                </div>
            </div>

            <p class="post-text mt-2">${post.content}</p>

            ${
                post.postType === "PROMOTIONAL"
                    ? `<a href="${post.ctaLink}" class="btn btn-sm btn-outline-primary">${post.ctaText}</a>`
                    : ""
            }

        </div>
    `;

    return card;
}
// ===============================
// FETCH GLOBAL FEED POSTS
// ===============================
function fetchFeedPosts() {

    const feedContainer = document.getElementById("feedContainer");
    if (!feedContainer) return;

    feedContainer.innerHTML = "<p class='text-center text-muted'>Loading feed...</p>";

    fetchSavedPosts()
        .then(() => fetch(`/posts/feed?viewerId=${currentUser.userId}`))
        .then(res => {
            if (!res.ok) throw new Error("Failed to load feed");
            return res.json();
        })
//        .then(posts => {
//            feedContainer.innerHTML = "";
//
//            if (!posts.length) {
//                feedContainer.innerHTML =
//                    "<div class='card feed-card text-center p-4'>No posts available</div>";
//                return;
//            }
//
//            posts.forEach(post => {
//                const card = createFeedPostCard(post, {
//                    showPinned: false,
//                    isSaved: savedPostIds.has(post.postId)
//                });
//                feedContainer.appendChild(card);
//            });
//        })
         .then(posts => {
                    allFeedPosts = posts;
                    renderFeedPosts();
                })
        .catch(err => {
            console.error(err);
            feedContainer.innerHTML =
                "<p class='text-center text-danger'>Error loading feed</p>";
        });
}

function renderFeedPosts() {

    const feedContainer = document.getElementById("feedContainer");
    if (!feedContainer) return;

    feedContainer.innerHTML = "";

    const filteredPosts = allFeedPosts.filter(post => {
        if (currentFeedFilter === "ALL") return true;
        return post.postType === currentFeedFilter;
    });

    if (!filteredPosts.length) {
        feedContainer.innerHTML =
            "<div class='card feed-card text-center p-4'>No posts found</div>";
        return;
    }

    filteredPosts.forEach(post => {
        const card = createFeedPostCard(post, {
            showPinned: false,
            isSaved: savedPostIds.has(post.postId)
        });
        feedContainer.appendChild(card);
    });
}

function setFeedFilter(filterType) {
    currentFeedFilter = filterType;
    updateFeedFilterUI();
    renderFeedPosts();
}

function updateFeedFilterUI() {
    document.querySelectorAll(".feed-filter-btn").forEach(btn => {
        btn.classList.remove("btn-primary");
        btn.classList.add("btn-outline-primary");
    });

    const activeBtn = document.querySelector(
        `.feed-filter-btn[data-filter="${currentFeedFilter}"]`
    );

    if (activeBtn) {
        activeBtn.classList.remove("btn-outline-primary");
        activeBtn.classList.add("btn-primary");
    }
}
// ===============================
// SAVE / UNSAVE POST
// ===============================
function savePost(postId, iconEl) {
    fetch(`/posts/${postId}/save?userId=${currentUser.userId}`, {
        method: "POST"
    })
    .then(res => {
        if (!res.ok) throw new Error("Already saved");
        savedPostIds.add(postId);
        iconEl.classList.replace("bi-bookmark", "bi-bookmark-fill");
        iconEl.onclick = () => unsavePost(postId, iconEl);
    })
    .catch(err => alert(err.message));
}

function unsavePost(postId, iconEl) {
    fetch(`/posts/${postId}/unsave?userId=${currentUser.userId}`, {
        method: "DELETE"
    })
    .then(res => {
        if (!res.ok) throw new Error("Failed to unsave");
        savedPostIds.delete(postId);
        iconEl.classList.replace("bi-bookmark-fill", "bi-bookmark");
        iconEl.onclick = () => savePost(postId, iconEl);
    })
    .catch(err => alert(err.message));
}

function fetchSavedPosts() {
    return fetch(`/posts/saved?userId=${currentUser.userId}`)
        .then(res => {
            if (!res.ok) throw new Error("Failed to fetch saved posts");
            return res.json();
        })
        .then(posts => {
            savedPostIds.clear();
            posts.forEach(p => savedPostIds.add(p.postId));
        });
}

// ===============================
// FETCH SAVED POSTS PAGE
// ===============================
function fetchSavedPostsPage() {

    const container = document.getElementById("savedPostsContainer");
    if (!container) return;

    container.innerHTML = "<p class='text-center text-muted'>Loading saved posts...</p>";

    fetch(`/posts/saved?userId=${currentUser.userId}`)
        .then(res => {
            if (!res.ok) throw new Error("Failed to load saved posts");
            return res.json();
        })
        .then(posts => {
            container.innerHTML = "";

            if (!posts || posts.length === 0) {
                container.innerHTML =
                    "<div class='card feed-card text-center p-4'>No saved posts</div>";
                return;
            }

            posts.forEach(post => {
                const card = createFeedPostCard(post, {
                    showPinned: false,
                    isSaved: true   // always saved on this page
                });

                // when unsaved → remove from UI
                const bookmarkIcon = card.querySelector(".bi-bookmark-fill");
                if (bookmarkIcon) {
                    bookmarkIcon.onclick = () => {
                        unsavePost(post.postId, bookmarkIcon);
                        card.remove();
                    };
                }

                container.appendChild(card);
            });
        })
        .catch(err => {
            console.error(err);
            container.innerHTML =
                "<p class='text-center text-danger'>Error loading saved posts</p>";
        });
}

// ===============================
// FETCH MY POSTS PAGE
// ===============================
function fetchMyPostsPage() {

    const container = document.getElementById("myPostsContainer");
    if (!container) return;

    container.innerHTML =
        "<p class='text-center text-muted'>Loading your posts...</p>";

    fetch(`/posts/my/data?userId=${currentUser.userId}`)
        .then(res => {
            if (!res.ok) throw new Error("Failed to load my posts");
            return res.json();
        })
        .then(posts => {
            container.innerHTML = "";

            if (!posts || posts.length === 0) {
                container.innerHTML =
                    "<div class='card feed-card text-center p-4'>You haven’t posted anything yet</div>";
                return;
            }

            posts.forEach(post => {
                const card = createMyPostCard(post);
                container.appendChild(card);
            });
        })
        .catch(err => {
            console.error(err);
            container.innerHTML =
                "<p class='text-center text-danger'>Error loading posts</p>";
        });
}

// ===============================
// MY POST ACTIONS
// ===============================
function pinPostAction(postId) {
    fetch(`/posts/${postId}/pin?userId=${currentUser.userId}`, {
        method: "PUT"
    })
    .then(res => {
        if (!res.ok) throw new Error("Pin failed");
        return res.json();
    })
    .then(() => {
        fetchMyPostsPage(); // ✅ FIXED
    })
    .catch(err => alert(err.message));
}

function unpinPostAction(postId) {
    fetch(`/posts/${postId}/unpin?userId=${currentUser.userId}`, {
        method: "PUT"
    })
    .then(res => {
        if (!res.ok) throw new Error("Unpin failed");
        return res.json();
    })
    .then(() => {
        fetchMyPostsPage(); // ✅ FIXED
    })
    .catch(err => alert(err.message));
}

function deletePostAction(postId) {
    if (!confirm("Delete this post?")) return;

    fetch(`/posts/${postId}?userId=${currentUser.userId}`, { method: "DELETE" })
        .then(() => location.reload())
        .catch(() => alert("Failed to delete post"));
}

function updatePost() {
    if (!window.POST_ID) return;

    const content = document.getElementById("postContent").value.trim();
    const postType = document.getElementById("postType")?.value || "NORMAL";
    const hashtagsInput = document.getElementById("hashtags")?.value || "";
    const ctaText = document.getElementById("ctaText")?.value;
    const ctaLink = document.getElementById("ctaLink")?.value;
    const scheduledAt = document.getElementById("scheduledAt")?.value;

    if (!content) {
        alert("Post content cannot be empty");
        return;
    }

    const hashtags = hashtagsInput
        .split(" ")
        .filter(tag => tag.startsWith("#"))
        .map(tag => tag.substring(1));

    const body = {
        content,
        postType,
        hashtags,
        ctaText,
        ctaLink,
        scheduledAt
    };

    fetch(`/posts/${POST_ID}?userId=${currentUser.userId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
    })
    .then(res => {
        if (!res.ok) throw new Error("Update failed");
        return res.json();
    })
    .then(() => {
        alert("Post updated successfully");
        window.location.href = "/posts";
    })
    .catch(err => alert(err.message));
}

//document.addEventListener("DOMContentLoaded", () => {
//    fetchFeedPosts();
//});