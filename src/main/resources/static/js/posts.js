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
    card.setAttribute("data-post-id", post.postId);

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
                <div class="shared-post-text mb-2">
                    🔁 ${post.sharedByUsername} shared
                    ${post.originalAuthorUsername}'s post
                </div>
            ` : ``}

           <!-- Post Header -->

           <div class="d-flex align-items-center mb-2">

               <!-- Avatar -->
               <div class="post-avatar me-2">
                   ${(post.username || "U").charAt(0).toUpperCase()}
               </div>

               <div class="flex-grow-1">

                   <div class="post-username">
                       ${post.isSharedPost
                           ? post.originalAuthorUsername
                           : post.username}
                   </div>

                   <div class="post-time">
                       ${formatDate(post.createdAt)}
                   </div>

               </div>

               ${options.showPinned && post.pinned
                   ? `<span class="text-warning">📌</span>`
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


<div class="post-actions d-flex justify-content-between text-center">


   <!-- LIKE -->
   <!-- LIKE -->
   <div class="col post-action like-action"
        onclick="likePostAction(${post.postId})">

       <i class="bi bi-hand-thumbs-up"></i>
       <span class="ms-1">Like</span>

       <span id="like-count-${post.postId}" class="like-count ms-1"
             onclick="event.stopPropagation(); toggleLikeList(event, ${post.postId})">

           ${post.likeCount || 0}

       </span>

   </div>


    <!-- COMMENT -->
    <div class="col post-action comment-action"
         onclick="toggleCommentBox(${post.postId})">

        <i class="bi bi-chat"></i>
        <span class="ms-1">Comment</span>

        <span class="comment-count ms-1">
            ${post.commentCount || 0}
        </span>

    </div>


    <!-- SHARE -->
    <!-- SHARE -->
    <div class="col post-action share-action"
         onclick="sharePostAction(${post.postId})">

        <i class="bi bi-arrow-repeat"></i>
        <span class="ms-1">Share</span>

        <span id="share-count-${post.postId}" class="share-count ms-1"
              onclick="event.stopPropagation(); toggleShareList(event, ${post.postId})">

            ${post.shareCount || 0}

        </span>

    </div>


    <!-- SAVE -->
    <div class="col post-action text-end">

        <i class="bi ${options.isSaved ? 'bi-bookmark-fill' : 'bi-bookmark'} save-icon"
           style="cursor:pointer"
           onclick="${
               options.isSaved
               ? `unsavePost(${post.postId}, this)`
               : `savePost(${post.postId}, this)`
           }">
        </i>

    </div>

</div>

            <!-- COMMENT BOX -->
            <div id="comment-box-${post.postId}"
                 class="comment-box"
                 style="display:none;">

                <input type="text"
                       id="comment-input-${post.postId}"
                       class="comment-input"
                       placeholder="Write a comment...">

                <button class="comment-btn"
                        onclick="submitCommentAction(${post.postId})">
                    Comment
                </button>

            </div>

            <!-- COMMENT LIST -->
            <div id="comment-list-${post.postId}" class="mt-2"></div>


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
        .then(() => fetch(`/posts/feed?viewerUserId=${currentUser.userId}`))
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

    fetch(`/posts/user?userId=${currentUser.userId}`)
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

            // pinned first
            posts.sort((a, b) => {
                if (a.pinned === b.pinned) {
                    return new Date(b.createdAt) - new Date(a.createdAt);
                }
                return b.pinned - a.pinned;
            });

            posts.forEach(post => {

                const card = createMyPostCard(post); // ⭐ IMPORTANT

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

    fetch(`/posts/${postId}?userId=${CURRENT_USER_ID}`, {
        method: "DELETE"
    })
    .then(response => {
        if (response.ok) {
            location.reload(); // refresh page
        } else {
            alert("Delete failed");
        }
    })
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
function likePostAction(postId) {

    fetch(`/posts/${postId}/like`, {
        method: "POST",
        credentials: "same-origin"
    })
    .then(res => {
        if (!res.ok) throw new Error("Like failed");
        return res.json();
    })
    .then(data => {

        const countSpan =
            document.getElementById(`like-count-${postId}`);

        if(countSpan){
            countSpan.innerText = data.likeCount;
        }

    })
    .catch(err => console.error("Like error:", err));
}
 // ===============================
 // SHARE POST
 // ===============================
function sharePostAction(postId) {

    fetch(`/posts/${postId}/share`, {
        method: "POST",
        credentials: "same-origin"
    })
    .then(res => {
        if (!res.ok) throw new Error("Share failed");
        return res.json();
    })
    .then(data => {

        const countSpan =
            document.getElementById(`share-count-${postId}`);

        if(countSpan){
            countSpan.innerText = data.shareCount;
        }

    })
    .catch(err => console.error("Share error:", err));
}
// ===============================
// TOGGLE COMMENT BOX
function toggleCommentBox(postId) {

    const commentBox =
        document.getElementById(`comment-box-${postId}`);

    const commentList =
        document.getElementById(`comment-list-${postId}`);



    if (!commentBox) return;

    const isHidden =
        commentBox.style.display === "none" ||
        commentBox.style.display === "";

    // 🔥 CLOSE LIKE + SHARE FIRST




    if (isHidden) {

        commentBox.style.display = "block";
        fetchComments(postId);

    } else {

        commentBox.style.display = "none";
        if (commentList) commentList.innerHTML = "";
    }
}


// ===============================
// SUBMIT COMMENT
// ===============================
function submitCommentAction(postId) {

    const input = document.getElementById(`comment-input-${postId}`);
    const commentText = input.value.trim();

    if (!commentText) {
        alert("Comment cannot be empty");
        return;
    }

   fetch(`/posts/${postId}/comments`, {
       method: "POST",
       credentials: "same-origin",
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

        // Clear input
        input.value = "";

        // Reload comments list 🔥
        fetchComments(postId);

        // Update comment count safely
        const commentCol = input.closest(".row")
            .querySelector(".post-action:nth-child(2) span");

        let currentCount = parseInt(commentCol.innerText) || 0;
        commentCol.innerText = currentCount + 1;

    })
    .catch(err => console.error(err));
}

//document.addEventListener("DOMContentLoaded", () => {
//    fetchFeedPosts();
//});

let deleteCommentId = null;
let deletePostId = null;

function deleteCommentAction(commentId, postId){

    deleteCommentId = commentId;
    deletePostId = postId;

    document.getElementById("deleteCommentOverlay").style.display = "flex";
}

function closeDeletePopup(){
    document.getElementById("deleteCommentOverlay").style.display = "none";
}

document.getElementById("confirmDeleteBtn").onclick = function(){

    fetch(`/posts/comments/${deleteCommentId}`, {
        method: "DELETE",
        credentials: "same-origin"
    })
    .then(res => res.text().then(text => ({ status: res.status, body: text })))
    .then(data => {

        closeDeletePopup();

        document.getElementById("successPopupMessage").innerText = data.body;
        document.getElementById("successPopupOverlay").style.display = "flex";

        if(data.status === 200){
            fetchComments(deletePostId);
        }

    })
    .catch(err => console.error(err));

};

function closeSuccessPopup(){
    document.getElementById("successPopupOverlay").style.display = "none";
}



function fetchComments(postId) {

    fetch(`/posts/${postId}/comments`)
    .then(res => {
        if (!res.ok) throw new Error("Failed to fetch comments");
        return res.json();
    })
    .then(comments => {

        const list = document.getElementById(`comment-list-${postId}`);
        list.innerHTML = "";

        if (!comments || comments.length === 0) {
            list.innerHTML = "<small class='text-muted'>No comments yet</small>";
            return;
        }

        comments.forEach(comment => {

            const div = document.createElement("div");
            div.className = "comment-item";

            div.innerHTML = `
            <div class="comment-avatar">
                ${comment.username.charAt(0).toUpperCase()}
            </div>

            <div class="comment-content">

                <div class="comment-header">

                    <span class="comment-user">${comment.username}</span>

                    <span class="comment-time">
                        ${new Date(comment.createdAt).toLocaleString()}
                    </span>

                    <span class="comment-delete"
                    onclick="deleteCommentAction(${comment.commentId}, ${postId})">
                    <i class="bi bi-trash"></i>
                    </span>

                </div>

                <div class="comment-text">
                    ${comment.commentText}
                </div>

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

    const profileUserId = window.PROFILE_USER_ID;

    if (!profileUserId) {
        container.innerHTML =
            "<p class='text-danger text-center'>User not found</p>";
        return;
    }

    fetch(`/posts/user?userId=${profileUserId}`)
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

    if(event) event.stopPropagation();

    const popup = document.getElementById("userPopupOverlay");
    const list = document.getElementById("popupUserList");
    const title = document.getElementById("popupTitle");

    if(!popup || !list || !title) return;

    title.innerText = "Liked by";
    list.innerHTML = "";

    fetch(`/posts/${postId}/likes`)
    .then(res => res.json())
    .then(users => {

        if(!Array.isArray(users) || users.length === 0){
            list.innerHTML = "<small>No likes yet</small>";
        } else {

            users.forEach(username => {

                const div = document.createElement("div");
                div.className = "user-popup-row";

                div.innerHTML = `
                    <div class="user-popup-avatar">
                        ${username.charAt(0).toUpperCase()}
                    </div>
                    <div>${username}</div>
                `;

                list.appendChild(div);
            });
        }

        popup.style.display = "flex";
    })
    .catch(err => console.error("Like popup error:", err));
}
function toggleShareList(event, postId) {

    if(event) event.stopPropagation();

    const popup = document.getElementById("userPopupOverlay");
    const list = document.getElementById("popupUserList");
    const title = document.getElementById("popupTitle");

    if(!popup || !list || !title) return;

    title.innerText = "Shared by";
    list.innerHTML = "";

    fetch(`/posts/${postId}/shares`)
    .then(res => res.json())
    .then(users => {

        if(!Array.isArray(users) || users.length === 0){
            list.innerHTML = "<small>No shares yet</small>";
        } else {

            users.forEach(username => {

                const div = document.createElement("div");
                div.className = "user-popup-row";

                div.innerHTML = `
                    <div class="user-popup-avatar">
                        ${username.charAt(0).toUpperCase()}
                    </div>
                    <div>${username}</div>
                `;

                list.appendChild(div);
            });
        }

        popup.style.display = "flex";
    })
    .catch(err => console.error("Share popup error:", err));
}





function closeUserPopup(){
    document.getElementById("userPopupOverlay").style.display = "none";
}

//////////////////////////////////////////////////////////
// LOAD POSTS BY HASHTAG (GLOBAL FUNCTION)
//////////////////////////////////////////////////////////

function loadHashtagPosts(tag) {

    const feedContainer = document.getElementById("feedContainer");

    if (!feedContainer) return;

    feedContainer.innerHTML =
        `<p class="text-center text-muted">Loading posts for #${tag}...</p>`;

    fetch(`/posts/hashtag/${tag}`)
        .then(res => {
            if (!res.ok) throw new Error("Failed to load hashtag posts");
            return res.json();
        })
        .then(posts => {

            feedContainer.innerHTML = "";

            if (!posts || posts.length === 0) {

                feedContainer.innerHTML =
                    `<div class="card feed-card text-center p-4">
                        No posts found for #${tag}
                     </div>`;

                return;
            }

            posts.forEach(post => {

                const card = createFeedPostCard(post, {
                    showPinned: false,
                    isSaved: savedPostIds.has(post.postId)
                });

                feedContainer.appendChild(card);

            });

        })
        .catch(err => {

            console.error(err);

            feedContainer.innerHTML =
                `<p class="text-center text-danger">
                    Error loading hashtag posts
                 </p>`;

        });
}
// ===============================
// LOAD FEED WHEN PAGE LOADS
// ===============================
document.addEventListener("DOMContentLoaded", function () {

    // refresh userId after page loads
    currentUser.userId = window.CURRENT_USER_ID;

    if (currentUser.userId) {
        fetchFeedPosts();
    }

});