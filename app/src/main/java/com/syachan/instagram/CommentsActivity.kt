package com.syachan.instagram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.syachan.instagram.adapter.CommentAdapter
import com.syachan.instagram.model.Comment
import com.syachan.instagram.model.User
import kotlinx.android.synthetic.main.activity_comments.*

class CommentsActivity : AppCompatActivity() {
    private var postId = ""
    private var publisherid = ""
    private var firebaseUser: FirebaseUser? = null
    private var commentAdapter: CommentAdapter? = null
    private var commentList: MutableList<Comment>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        val intent = intent
        postId = intent.getStringExtra("postId")
        publisherid = intent.getStringExtra("publisherId")

        firebaseUser = FirebaseAuth.getInstance().currentUser

        var recyclerView: RecyclerView? = null

        recyclerView = findViewById(R.id.recycler_comment)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager

        commentList = ArrayList()
        commentAdapter = CommentAdapter(this, commentList as ArrayList<Comment>)
        recyclerView.adapter = commentAdapter

        userInfo()
        getPostImageComent()
        readComment()

        txtpost_comment.setOnClickListener {
            if (add_comment!!.text.toString() == ""){
                Toast.makeText(this,"Please write comment first", Toast.LENGTH_SHORT).show()

            } else {
                addComment()
            }
        }
    }

    private fun getPostImageComent() {
        val postCommentRef = FirebaseDatabase.getInstance().reference
            .child("Posts").child(postId).child("postimage")

        postCommentRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val image = p0.value.toString()

                    Picasso.get().load(image).placeholder(R.drawable.profile)
                        .into(post_image_comment)
                }
            }

        })
    }
    private fun readComment() {
        var commentRef = FirebaseDatabase.getInstance().reference
            .child("Comments").child(postId)

        commentRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    commentList!!.clear()
                    for (snapshot in p0.children) {
                        val comment = snapshot.getValue(Comment::class.java)

                        commentList!!.add(comment!!)
                    }

                    commentAdapter!!.notifyDataSetChanged()
                }
            }

        })

    }
    private fun addComment() {
        val commentRef = FirebaseDatabase.getInstance().reference
            .child("Comments").child(postId!!)

        val commentMap = HashMap<String, Any>()
        commentMap["comment"] = add_comment.text.toString()
        commentMap["publisher"] = firebaseUser!!.uid

        commentRef.push().setValue(commentMap)

        add_comment!!.text.clear()
    }
    private fun userInfo() {
        val userRef = FirebaseDatabase.getInstance().reference
            .child("Users").child(firebaseUser!!.uid)

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                        .into(profile_image_comment)

                }
            }

        })



    }
}