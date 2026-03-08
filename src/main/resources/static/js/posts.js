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
    const cardId = `post-${post.postId}`;
    const card = document.createElement("div");
    card.className = "card feed-card mb-3";

    const hashtags = (post.hashtags || [])
        .map(tag => `<span class="text-primary me-2">#${tag}</span>`)
        .join("");

    const tagsHtml = (post.tags || [])
        .map(tag => `
            <span class="badge me-2
                ${tag.tagType === 'PRODUCT' ? 'bg-info' : 'bg-success'}">
                ${tag.tagType}: ${tag.tagName}
            </span>
        `)
        .join("");

    card.innerHTML = `
        <div class="card-body">

            <!-- 🔁 Shared Banner -->
            ${post.isSharedPost ? `
                <div class="text-muted small mb-2">
                    🔁 ${post.sharedByUsername} shared
                    ${post.originalAuthorUsername}'s post
                </div>
            ` : ``}

            <!-- Post Header -->
            <div class="d-flex justify-content-between">
                <strong>
                    ${post.isSharedPost
                        ? post.originalAuthorUsername
                        : post.username}
                </strong>

                ${options.showPinned && post.pinned
                    ? `<span class="text-warning">📌 Pinned</span>`
                    : ""}
            </div>

            <!-- Post Content -->
            <p class="post-text mt-2">${post.content}</p>

            ${hashtags ? `<div class="mb-2">${hashtags}</div>` : ""}

            ${tagsHtml ? `<div class="mt-2">${tagsHtml}</div>` : ""}

            ${
                post.postType === "PROMOTIONAL" &&
                post.ctaText &&
                post.ctaLink
                ? `
               <div class="mt-2">
                   ${
                       post.postType === "PROMOTIONAL" &&
                       post.ctaText &&
                       post.ctaLink
                       ? `
                       <div class="mt-2">
                           <a href="${post.ctaLink}"
                              target="_blank"
                              class="btn btn-sm btn-primary">
                               ${post.ctaText}
                           </a>
                       </div>
                       `
                       : ""
                   }
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

            <!-- ACTION ROW -->
            <div class="row text-center align-items-center">

               <!-- LIKE -->
               <!-- LIKE -->
               <div class="col post-action like-action"
                    style="cursor:pointer"
                    onclick="likePostAction(${post.postId}, this)">
                    👍
                    <span class="like-count"
                          style="cursor:pointer; font-weight:600;"
                          onclick="toggleLikeList(event, ${post.postId})">
                          ${post.likeCount || 0}
                    </span>
               </div>

                <!-- COMMENT -->
               <div class="col post-action"
                    style="cursor:pointer"
                    onclick="toggleCommentBox('${cardId}', ${post.postId})">
                    💬 <span>${post.commentCount || 0}</span>
               </div>

                <!-- SHARE -->
                <div class="col post-action"
                     style="cursor:pointer"
                     onclick="sharePostAction(${post.postId}, this)">
                     🔗
                     <span style="cursor:pointer; font-weight:600;"
                           onclick="toggleShareList(event, ${post.postId})">
                           ${post.shareCount || 0}
                     </span>
                </div>

                <!-- SAVE -->
                <div class="col post-action text-end">
                    <i class="bi ${options.isSaved ? 'bi-bookmark-fill' : 'bi-bookmark'}"
                       style="cursor:pointer;font-size:18px"
                       onclick="${
                           options.isSaved
                           ? `unsavePost(${post.postId}, this)`
                           : `savePost(${post.postId}, this)`
                       }"
                       title="Save post"></i>
                </div>

            </div>

            <!-- COMMENT BOX -->
            <div id="comment-box-${cardId}"
                 class="mt-2"
                 style="display:none;">

                <input type="text"
                       id="comment-input-${cardId}"
                       class="form-control form-control-sm"
                       placeholder="Write a comment...">

                <button class="btn btn-sm btn-primary mt-1"
                        onclick="submitCommentAction('${cardId}', ${post.postId})">
                    Post
                </button>
            </div>

            <!-- COMMENT LIST -->

            <div id="comment-list-${cardId}" class="mt-2"></div>
            <!-- LIKE LIST -->
            <div id="like-list-${post.postId}"
                 class="mt-2"
                 style="display:none;"></div>
        </div>
            <div id="share-list-${post.postId}"
                 class="mt-2"
                 style="display:none;"></div>
    `;
const likeDiv = card.querySelector(".like-action");
const likeCountSpan = card.querySelector(".like-count");

if (likeDiv) {
    likeDiv.addEventListener("click", function () {
        likePostAction(post.postId, likeDiv);
    });
}

if (likeCountSpan) {
    likeCountSpan.addEventListener("click", function (event) {
        event.stopPropagation();   // prevents like toggle
        showLikes(post.postId);
    });
}

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

// ===============================
// LIKE POST
// ===============================
function likePostAction(postId, element) {

    fetch(`/posts/${postId}/like`, {
        method: "POST"
    })
    .then(res => {
        if (!res.ok) throw new Error("Like failed");
        return res.json();
    })
    .then(newCount => {

        const countSpan = element.querySelector("span");
        countSpan.innerText = newCount;

    })
    .catch(err => console.error(err));
}


// ===============================
// SHARE POST
// ===============================
function sharePostAction(postId, element) {

    fetch(`/posts/${postId}/share`, {
        method: "POST"
    })
    .then(res => {
        if (!res.ok) throw new Error("Share failed");
        return res.json();   // returns updated count
    })
    .then(newCount => {

        const countSpan = element.querySelector("span");
        countSpan.innerText = newCount;

        element.classList.add("shared");

        // 🔥 REFRESH FEED
        fetchFeedPosts();

    })
    .catch(err => console.error(err));
}


// ===============================
// TOGGLE COMMENT BOX
function toggleCommentBox(cardId, postId) {

    const commentBox =
        document.getElementById(`comment-box-${cardId}`);

    const commentList =
        document.getElementById(`comment-list-${cardId}`);

    const likeContainer =
        document.getElementById(`like-list-${postId}`);

    const shareContainer =
        document.getElementById(`share-list-${postId}`);

    if (!commentBox) return;

    const isHidden =
        commentBox.style.display === "none" ||
        commentBox.style.display === "";

    if (likeContainer) {
        likeContainer.style.display = "none";
        likeContainer.innerHTML = "";
    }

    if (shareContainer) {
        shareContainer.style.display = "none";
        shareContainer.innerHTML = "";
    }

    if (isHidden) {

        commentBox.style.display = "block";
        fetchComments(postId, cardId);

    } else {

        commentBox.style.display = "none";
        if (commentList) commentList.innerHTML = "";
    }
}


// ===============================
// SUBMIT COMMENT
// ===============================
function submitCommentAction(cardId, postId) {

    const input = document.getElementById(`comment-input-${cardId}`);
    if (!input) return;

    const commentText = input.value.trim();

    if (!commentText) {
        alert("Comment cannot be empty");
        return;
    }

    fetch(`/posts/${postId}/comments`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(commentText)
    })
    .then(res => {
        if (!res.ok) throw new Error("Comment failed");
        return res.text();
    })
    .then(() => {

        // clear input
        input.value = "";

        // reload comments for THIS card
        fetchComments(postId, cardId);

        // update comment count
        const commentCounter =
            document.querySelector(
                `[onclick="toggleCommentBox('${cardId}', ${postId})"] span`
            );

        if (commentCounter) {
            let currentCount = parseInt(commentCounter.innerText) || 0;
            commentCounter.innerText = currentCount + 1;
        }

    })
    .catch(err => console.error(err));
}

//document.addEventListener("DOMContentLoaded", () => {
//    fetchFeedPosts();
//});

function fetchComments(postId, cardId = postId) {

    fetch(`/posts/${postId}/comments`)
    .then(res => {
        if (!res.ok) throw new Error("Failed to fetch comments");
        return res.json();
    })
    .then(comments => {

        const list = document.getElementById(`comment-list-${cardId}`);
        if (!list) return;

        list.innerHTML = "";

        if (!comments || comments.length === 0) {
            list.innerHTML = "<small class='text-muted'>No comments yet</small>";
            return;
        }

        comments.forEach(comment => {

            const div = document.createElement("div");
            div.className = "border rounded p-2 mb-2 bg-light";

            div.innerHTML = `
                <div class="d-flex justify-content-between align-items-start">

                    <div>
                        <strong>${comment.username}</strong>
                        <small class="text-muted ms-2">
                            ${new Date(comment.createdAt).toLocaleString()}
                        </small>

                        <div class="mt-1">${comment.commentText}</div>
                    </div>

                    <button class="btn btn-sm btn-link text-danger"
                            onclick="openDeleteModal(${comment.commentId}, ${postId}, '${cardId}')">
                        <i class="bi bi-trash"></i>
                    </button>

                </div>
            `;

            list.appendChild(div);
        });

    })
    .catch(err => console.error(err));
}
// ===============================
// FETCH PROFILE POSTS
// ===============================
function fetchProfilePosts() {

    const container = document.getElementById("profilePostsContainer");
    if (!container) return;

    container.innerHTML = "<p class='text-center text-muted'>Loading posts...</p>";

    fetch(`/posts/my/data?userId=${currentUser.userId}&t=${Date.now()}`)
        .then(res => {
            if (!res.ok) throw new Error("Failed to load profile posts");
            return res.json();
        })
        .then(posts => {

            container.innerHTML = "";

            if (!posts || posts.length === 0) {
                container.innerHTML =
                    "<div class='card text-center p-4'>No posts yet</div>";
                return;
            }

            posts.forEach(post => {

                const card = createFeedPostCard(post, {
                    showPinned: true,
                    isSaved: false
                });

                container.appendChild(card);

                const commentBox = card.querySelector('[id^="comment-box-"]');
                if (!commentBox) return;

                const cardId = commentBox.id.replace("comment-box-", "");

                // show comment section
                commentBox.style.display = "block";

                // load comments
                fetchComments(post.postId, cardId);

            });

        })
        .catch(err => {
            console.error(err);
            container.innerHTML =
                "<p class='text-center text-danger'>Error loading posts</p>";
        });
}
        // ===============================
        // SHOW USERS WHO LIKED
        // ===============================
//function showLikes(postId) {
//
//    fetch(`/posts/${postId}/likes`)
//    .then(res => res.json())
//    .then(users => {
//
//        if (!users || users.length === 0) {
//            alert("No likes yet");
//            return;
//        }
//
//        alert("Liked by:\n\n" + users.join("\n"));
//    })
//    .catch(err => console.error(err));
//}

function toggleLikeList(event, postId) {

    event.stopPropagation();

    const likeContainer =
        document.getElementById(`like-list-${postId}`);

    const shareContainer =
        document.getElementById(`share-list-${postId}`);

    const commentBox =
        document.getElementById(`comment-box-${postId}`);

    const commentList =
        document.getElementById(`comment-list-${postId}`);

    if (!likeContainer) return;

    const isHidden =
        likeContainer.style.display === "none" ||
        likeContainer.style.display === "";

    // 🔥 CLOSE SHARE
    if (shareContainer) {
        shareContainer.style.display = "none";
        shareContainer.innerHTML = "";
    }

    // 🔥 CLOSE COMMENT
    if (commentBox) {
        commentBox.style.display = "none";
    }

    if (commentList) {
        commentList.innerHTML = "";
    }

    if (isHidden) {

        likeContainer.style.display = "block";

        fetch(`/posts/${postId}/likes`)
        .then(res => res.json())
        .then(users => {

            likeContainer.innerHTML = "";

            if (!users || users.length === 0) {
                likeContainer.innerHTML =
                    "<small class='text-muted'>No likes yet</small>";
                return;
            }

            users.forEach(username => {

                const div = document.createElement("div");
                div.className =
                    "border rounded p-2 mb-2 bg-light";

                div.innerHTML =
                    `<strong>${username}</strong>`;

                likeContainer.appendChild(div);
            });

        });

    } else {

        likeContainer.style.display = "none";
        likeContainer.innerHTML = "";
    }
}

function toggleShareList(event, postId) {

    event.stopPropagation();

    const shareContainer =
        document.getElementById(`share-list-${postId}`);

    const likeContainer =
        document.getElementById(`like-list-${postId}`);

    const commentBox =
        document.getElementById(`comment-box-${postId}`);

    const commentList =
        document.getElementById(`comment-list-${postId}`);

    if (!shareContainer) return;

    const isHidden =
        shareContainer.style.display === "none" ||
        shareContainer.style.display === "";

    // 🔥 CLOSE LIKE
    if (likeContainer) {
        likeContainer.style.display = "none";
        likeContainer.innerHTML = "";
    }

    // 🔥 CLOSE COMMENT
    if (commentBox) {
        commentBox.style.display = "none";
    }

    if (commentList) {
        commentList.innerHTML = "";
    }

    if (isHidden) {

        shareContainer.style.display = "block";

        fetch(`/posts/${postId}/shares`)
        .then(res => res.json())
        .then(users => {

            shareContainer.innerHTML = "";

            if (!users || users.length === 0) {
                shareContainer.innerHTML =
                    "<small class='text-muted'>No shares yet</small>";
                return;
            }

            users.forEach(username => {

                const div = document.createElement("div");
                div.className =
                    "border rounded p-2 mb-2 bg-light";

                div.innerHTML =
                    `<strong>${username}</strong>`;

                shareContainer.appendChild(div);
            });

        });

    } else {

        shareContainer.style.display = "none";
        shareContainer.innerHTML = "";
    }
}

function deleteComment(commentId, postId) {

    if (!confirm("Delete this comment?")) return;

    fetch(`/posts/comments/${commentId}`, {
        method: "DELETE"
    })
    .then(res => {
        if (!res.ok) throw new Error("Delete failed");

        // reload comments
        fetchComments(postId);
    })
    .catch(err => console.error(err));
}

let deleteCommentId = null;
let deletePostId = null;
let deleteCardId = null;

function openDeleteModal(commentId, postId, cardId) {

    deleteCommentId = commentId;
    deletePostId = postId;
    deleteCardId = cardId;

    const modal = new bootstrap.Modal(
        document.getElementById("deleteCommentModal")
    );

    modal.show();
}

document.addEventListener("DOMContentLoaded", () => {

    const confirmBtn = document.getElementById("confirmDeleteComment");

    if (!confirmBtn) return;

    confirmBtn.addEventListener("click", function () {

        fetch(`/posts/comments/${deleteCommentId}`, {
            method: "DELETE"
        })
        .then(res => {

            if (!res.ok) {
                throw new Error("Not allowed");
            }

            return res.text();
        })
        .then(() => {

            const modalEl = document.getElementById("deleteCommentModal");
            const modal = bootstrap.Modal.getInstance(modalEl);
            modal.hide();

            showCenterMessage("Comment deleted successfully");

            // PROFILE PAGE
            if (document.getElementById("profilePostsContainer")) {

                // clear container first
                const container = document.getElementById("profilePostsContainer");
                container.innerHTML = "<p class='text-muted text-center'>Updating...</p>";

                // reload profile posts
                fetchProfilePosts();

            } else {

                // DASHBOARD
                fetchComments(deletePostId, deleteCardId);

                const card = document.getElementById(`comment-box-${deleteCardId}`)?.closest(".card");

                if (card) {
                    const counter = card.querySelector(".post-action:nth-child(2) span");

                    if (counter) {
                        let count = parseInt(counter.innerText) || 0;
                        if (count > 0) counter.innerText = count - 1;
                    }
                }
            }
        })
        .catch(() => {

            showCenterMessage("You cannot delete this comment");

        });

    });

});


function showCenterMessage(message) {

    const box = document.getElementById("centerMessage");

    if (!box) {
        console.log(message);
        return;
    }

    box.innerText = message;
    box.style.display = "block";

    setTimeout(() => {
        box.style.display = "none";
    }, 2000);
}