package com.olacapstone.socialbackend.controller;

import com.olacapstone.socialbackend.entity.DoubleIdObjectEntity;
import com.olacapstone.socialbackend.entity.IdObjectEntity;
import com.olacapstone.socialbackend.entity.PostEntity;
import com.olacapstone.socialbackend.service.PostService;
import com.olacapstone.socialbackend.service.ResponseObjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class PostController {
    @Autowired
    private PostService postService;

    @PostMapping("/insertpost")
    public ResponseEntity<ResponseObjectService> insertPost(@RequestBody PostEntity inputPost) {
        return new ResponseEntity<ResponseObjectService>(postService.insertPost(inputPost), HttpStatus.OK);
    }
    
    @PostMapping("/myposts")
    public ResponseEntity<ResponseObjectService> findPostByUserId(@RequestBody IdObjectEntity inputUserId) {
        return new ResponseEntity<ResponseObjectService>(postService.findPostByUserId(inputUserId), HttpStatus.OK);
    }

    @PostMapping("/followingposts")
    public ResponseEntity<ResponseObjectService> findPostByFollowing(@RequestBody IdObjectEntity inputUserId) {
        return new ResponseEntity<ResponseObjectService>(postService.findPostByFollowing(inputUserId), HttpStatus.OK);
    }

    // currently not in use, post is update via comment controller
    // @PutMapping("/updatebycomment")
    // public ResponseEntity<ResponseObjectService> updateByComment(@RequestBody PostEntity inputPost) {
    //     return new ResponseEntity<ResponseObjectService>(postService.updatePostByComment(inputPost), HttpStatus.OK);
    // }

    @PostMapping("/likepost")
    public ResponseEntity<ResponseObjectService> likePost(@RequestBody DoubleIdObjectEntity doubleId) {
        return new ResponseEntity<ResponseObjectService>(postService.updatePostByLike(doubleId), HttpStatus.OK);
    }

    @PostMapping("/deletepost")
    public ResponseEntity<ResponseObjectService> deletePost(@RequestBody DoubleIdObjectEntity doubleId) {
        return new ResponseEntity<ResponseObjectService>(postService.deletePost(doubleId), HttpStatus.OK);
    }

    @PostMapping("/dislikepost")
    public ResponseEntity<ResponseObjectService> dislikePost(@RequestBody DoubleIdObjectEntity doubleId) {
        return new ResponseEntity<ResponseObjectService>(postService.updatePostByDislike(doubleId), HttpStatus.OK);
    }

//    @PostMapping("/sharepost")
//    public ResponseEntity<ResponseObjectService> sharePost(@RequestBody DoubleIdObjectEntity doubleId) {
//        return new ResponseEntity<ResponseObjectService>(postService.updatePostByShare(doubleId), HttpStatus.OK);
//    }
}
