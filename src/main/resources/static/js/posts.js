// ===============================
// GLOBAL STATE
// ===============================
let currentUser = null;

// ===============================
// ON PAGE LOAD
// ===============================
document.addEventListener("DOMContentLoaded", () => {
    fetchLoggedInUser();
    setupPostTypeToggle();
});

// ===============================
// SETUP CTA TOGGLE
// ===============================
function setupPostTypeToggle() {
    const postType = document.getElementById("postType");
    const ctaSection = document.getElementById("ctaSection");

    if (!postType || !ctaSection) return;

    postType.addEventListener("change", () => {
        if (postType.value === "PROMOTIONAL") {
            ctaSection.classList.remove("d-none");
        } else {
            ctaSection.classList.add("d-none");
        }
    });
}

// ===============================
// FETCH LOGGED-IN USER
// ===============================
function fetchLoggedInUser() {
    fetch("/api/auth/me")
        .then(response => response.json())
        .then(data => {
            if (!data || !data.authenticated) {
                console.error("User not authenticated");
                return;
            }

            currentUser = data;
            fetchMyPosts();
        })
        .catch(error => {
            console.error("Error fetching user info:", error);
        });
}

// ===============================
// FETCH USER POSTS
// ===============================
function fetchMyPosts() {
    if (!currentUser) return;

    fetch(`/api/posts/my?userId=${currentUser.userId}`)
        .then(response => response.json())
        .then(posts => {
            renderPosts(posts);
        })
        .catch(error => {
            console.error("Error fetching posts:", error);
        });
}

// ===============================
// RENDER POSTS
// ===============================
function renderPosts(posts) {
    const feedContainer = document.getElementById("postFeedContainer");
    if (!feedContainer) return;

    feedContainer.innerHTML = "";

    if (!posts || posts.length === 0) {
        feedContainer.innerHTML = `
            <div class="text-center text-muted">
                <p class="small">No posts yet. Start sharing your thoughts ✨</p>
            </div>
        `;
        return;
    }

    posts.forEach(post => {
        const postCard = createPostCard(post);
        feedContainer.appendChild(postCard);
    });
}

// ===============================
// CREATE POST CARD
// ===============================
function createPostCard(post) {
    const card = document.createElement("div");
    card.className = "card shadow-sm mb-3";

    const hashtagsHtml = (post.hashtags || [])
        .map(tag => `<span class="badge bg-light text-primary me-1">#${tag}</span>`)
        .join("");

    card.innerHTML = `
        <div class="card-body">
            <div class="d-flex justify-content-between mb-2">
                <strong>${post.username}</strong>
                <small class="text-muted">${formatDate(post.createdAt)}</small>
            </div>

            <p class="mb-2">${post.content}</p>

            <div class="mb-2">
                ${hashtagsHtml}
            </div>

            ${
                post.userId === currentUser.userId
                    ? `<div class="text-end">
                           <button class="btn btn-sm btn-outline-danger"
                                   onclick="deletePost(${post.postId})">
                               Delete
                           </button>
                       </div>`
                    : ""
            }
        </div>
    `;

    return card;
}

// ===============================
// CREATE NEW POST
// ===============================
function createPost() {
    if (!currentUser) return;

    const content = document.getElementById("postContent")?.value.trim();
    const postType = document.getElementById("postType")?.value;
    const hashtagsInput = document.getElementById("hashtags")?.value || "";
    const ctaText = document.getElementById("ctaText")?.value.trim();
    const ctaLink = document.getElementById("ctaLink")?.value.trim();

    if (!content) {
        alert("Post content cannot be empty");
        return;
    }

    if (postType === "PROMOTIONAL" && (!ctaText || !ctaLink)) {
        alert("CTA Text and CTA Link are required for promotional posts");
        return;
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
    }

    fetch(`/api/posts?userId=${currentUser.userId}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(requestBody)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to create post");
            }
            return response.json();
        })
        .then(() => {
            resetCreatePostForm();
            fetchMyPosts();
        })
        .catch(err => alert(err.message));
}

// ===============================
// RESET FORM
// ===============================
function resetCreatePostForm() {
    const fields = [
        "postContent",
        "hashtags",
        "ctaText",
        "ctaLink"
    ];

    fields.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = "";
    });
}

// ===============================
// DELETE POST
// ===============================
function deletePost(postId) {
    if (!currentUser) return;

    if (!confirm("Are you sure you want to delete this post?")) {
        return;
    }

    fetch(`/api/posts/${postId}?userId=${currentUser.userId}`, {
        method: "DELETE"
    })
        .then(() => {
            fetchMyPosts();
        })
        .catch(error => {
            console.error("Error deleting post:", error);
        });
}

// ===============================
// FORMAT DATE
// ===============================
function formatDate(dateTime) {
    if (!dateTime) return "";
    return new Date(dateTime).toLocaleString();
}