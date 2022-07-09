package com.olacapstone.socialbackend.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.olacapstone.socialbackend.entity.DoubleIdObjectEntity;
import com.olacapstone.socialbackend.entity.IdObjectEntity;
import com.olacapstone.socialbackend.entity.PostByFollowing;
import com.olacapstone.socialbackend.entity.PostEntity;
import com.olacapstone.socialbackend.entity.UserEntity;
import com.olacapstone.socialbackend.repository.PostRepository;
import com.olacapstone.socialbackend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepo;
    @Autowired
    private UserRepository userRepo;
    
    public ResponseObjectService insertPost(PostEntity inputPost) {
        ResponseObjectService responseObj = new ResponseObjectService();
        inputPost.setCreatedAt(Instant.now());
        responseObj.setStatus("success");
        responseObj.setMessage("success");
        responseObj.setPayload(postRepo.save(inputPost));
        return responseObj;
    }

    public ResponseObjectService findPostByUserId(IdObjectEntity inputUserId) {
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<List<PostEntity>> userPostsOpt = postRepo.findByUserIdOrderByCreatedAtDesc(inputUserId.getId());
        if (userPostsOpt.isEmpty()) {
            responseObj.setStatus("fail");
            responseObj.setMessage("cannot find any post from user id: " + inputUserId.getId());
            responseObj.setPayload(null);
            return responseObj;
        } else {
            List<PostEntity> userPosts = userPostsOpt.get();
            responseObj.setStatus("success");
            responseObj.setMessage("success");
            responseObj.setPayload(userPosts);
            return responseObj;
        }
    }
    
    public ResponseObjectService findPostByFollowing(IdObjectEntity inputUserId) {
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<UserEntity> optUser = userRepo.findById(inputUserId.getId());
        if (optUser.isEmpty()) {
            responseObj.setStatus("fail");
            responseObj.setMessage("cannot find any post from user id: " + inputUserId.getId());
            responseObj.setPayload(null);
            return responseObj;
        } else {
            UserEntity user = optUser.get();
            if (user.getFollowing() != null) {
                // if user followed someone, get their ids
                List<String> followingIds = new ArrayList<>();
                for (String id : user.getFollowing()) {
                    followingIds.add(id);
                }
                // based on these ids, get their equivalent posts
                List<PostByFollowing> listPosts = new ArrayList<>();
                for (String followingId : followingIds) {
                    // get following user info based on Id
                    UserEntity followingUser = new UserEntity();
                    Optional<UserEntity> optFollowingUser = userRepo.findById(followingId);
                    if (optFollowingUser.isPresent()) {
                        followingUser = optFollowingUser.get();
                    }

                    followingUser.setPassword("");
                    
                    // get equivalent posts
                    Optional<List<PostEntity>> followingPostsOpt = postRepo.findByUserId(followingId);
                    if (followingPostsOpt.isPresent()) {
                        // if followed account has any post, collect them
                        List<PostEntity> followingPosts = followingPostsOpt.get();
                        if (followingPosts != null) {
                            for (PostEntity item : followingPosts) {
                                listPosts.add(new PostByFollowing(followingUser, item));
                            }
                        }
                    }
                }
                Collections.sort(listPosts, (o1, o2) -> o2.getPost().getCreatedAt().compareTo(o1.getPost().getCreatedAt()));
                responseObj.setStatus("success");
                responseObj.setMessage("success");
                responseObj.setPayload(listPosts);
                return responseObj;
            } else {
                responseObj.setStatus("fail");
                responseObj.setMessage("user id: " + inputUserId.getId() + " has empty following list");
                responseObj.setPayload(null);
                return responseObj;
            }
        }
    }

    public ResponseObjectService updatePostByComment(PostEntity inputPost) {
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<PostEntity> optPost = postRepo.findById(inputPost.getId());
        if (optPost.isEmpty()) {
            responseObj.setStatus("fail");
            responseObj.setMessage("cannot find post id: " + inputPost.getId());
            responseObj.setPayload(null);
            return responseObj;
        } else {
            // inputPost.setCreatedAt(Instant.now());
            postRepo.save(inputPost);
            responseObj.setStatus("success");
            responseObj.setMessage("post is updated successfully");
            responseObj.setPayload(inputPost);
            return responseObj;
        }
    }

    public ResponseObjectService deletePost(DoubleIdObjectEntity doubleId) {
        ResponseObjectService responseObj = new ResponseObjectService();
        try{
            postRepo.deleteById(doubleId.getId1());
            responseObj.setStatus("success");
            responseObj.setMessage("Post deleted");
            return responseObj;
        } catch (Exception e){
            responseObj.setStatus("fail");
            responseObj.setMessage("cannot delete post id: " + doubleId.getId1());
            responseObj.setPayload(null);
            return responseObj;
        }
    }

    public ResponseObjectService updatePostByLike(DoubleIdObjectEntity doubleId) {
        // id 1 - post Id, id 2 - user who liked post
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<PostEntity> optPost = postRepo.findById(doubleId.getId1());
        if (optPost.isEmpty()) {
            responseObj.setStatus("fail");
            responseObj.setMessage("cannot find post id: " + doubleId.getId1());
            responseObj.setPayload(null);
            return responseObj;
        } else {
            PostEntity targetPost = optPost.get();
            List<String> likeList = targetPost.getLike();
            if (likeList == null) {
                likeList = new ArrayList<>();
            }

            if (!likeList.contains(doubleId.getId2())) {
                likeList.add(doubleId.getId2());
            } else {
                likeList.remove(doubleId.getId2());
            }
            targetPost.setLike(likeList);
            postRepo.save(targetPost);
            responseObj.setStatus("success");
            responseObj.setMessage("update like to the target post id: " + targetPost.getId());
            responseObj.setPayload(targetPost);
            return responseObj;
        }
    }

    public ResponseObjectService updatePostByDislike(DoubleIdObjectEntity doubleId) {
        // id 1 - post Id, id 2 - user who disliked post
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<PostEntity> optPost = postRepo.findById(doubleId.getId1());
        if (optPost.isEmpty()) {
            responseObj.setStatus("fail");
            responseObj.setMessage("cannot find post id: " + doubleId.getId1());
            responseObj.setPayload(null);
            return responseObj;
        } else {
            PostEntity targetPost = optPost.get();
            List<String> dislikeList = targetPost.getDislike();
            if (dislikeList == null) {
                dislikeList = new ArrayList<>();
            }
            // dislike and undislike a post
            if (!dislikeList.contains(doubleId.getId2())) {
                dislikeList.add(doubleId.getId2());
            } else {
                dislikeList.remove(doubleId.getId2());
            }
            targetPost.setDislike(dislikeList);
            postRepo.save(targetPost);
            responseObj.setStatus("success");
            responseObj.setMessage("update dislike to the target post id: " + targetPost.getId());
            responseObj.setPayload(targetPost);
            return responseObj;
        }
    }
}
