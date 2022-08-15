package ir.MrMohamadHosein.studentManager_Rxjava

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.google.gson.JsonObject
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ir.MrMohamadHosein.studentManager_Rxjava.databinding.ActivityMain2Binding
import ir.MrMohamadHosein.studentManager_Rxjava.net.ApiService
import ir.MrMohamadHosein.studentManager_Rxjava.recycler.Student
import ir.MrMohamadHosein.studentManager_Rxjava.util.BASE_URL
import ir.MrMohamadHosein.studentManager_Rxjava.util.asyncRequest
import ir.MrMohamadHosein.studentManager_Rxjava.util.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity2 : AppCompatActivity() {
    lateinit var binding: ActivityMain2Binding
    lateinit var apiService: ApiService
    var isInserting = true
    lateinit var disposable: Disposable
    private val compositeDisposable = CompositeDisposable()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMain2)

        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.edtFirstName.requestFocus()

        val retrofit = Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

            .build()
        apiService = retrofit.create(ApiService::class.java)

        val testMode = intent.getParcelableExtra<Student>("student")
        isInserting = (testMode == null)

        if (!isInserting) {

            binding.btnDone.text = "update"

            val dataFromIntent = intent.getParcelableExtra<Student>("student")!!
            binding.edtScore.setText(dataFromIntent.score.toString())
            binding.edtCourse.setText(dataFromIntent.course)

            val splitedName = dataFromIntent.name.split(" ")
            binding.edtFirstName.setText(splitedName[0])
            binding.edtLastName.setText(splitedName[(splitedName.size - 1)])

        }

        binding.btnDone.setOnClickListener {

            if (isInserting) {
                addNewStudent()
            } else {
                updateStudent(testMode!!)
            }

        }


    }



    private fun updateStudent(student: Student) {

        val firstName = binding.edtFirstName.text.toString()
        val lastName = binding.edtLastName.text.toString()
        val score = binding.edtScore.text.toString()
        val course = binding.edtCourse.text.toString()

        if (
            firstName.isNotEmpty() &&
            lastName.isNotEmpty() &&
            course.isNotEmpty() &&
            score.isNotEmpty()
        ) {

            val jsonObject = JsonObject()
            jsonObject.addProperty("name", firstName + " " + lastName)
            jsonObject.addProperty("course", course)
            jsonObject.addProperty("score", score.toInt())


            /*apiService
                .updateStudent(firstName + " " + lastName, jsonObject)
                .enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {

                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                    }
                })*/


            apiService
                .updateStudent(student.id, jsonObject)
                .asyncRequest()
                .subscribe( object : CompletableObserver {

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onComplete() {
                        showToast("student updated :)")
                        onBackPressed()
                    }

                    override fun onError(e: Throwable) {
                        showToast("error -> " + e.message ?: "null")
                    }

                } )



        } else {
            Toast.makeText(this, "لطفا اطلاعات را کامل وارد کنید", Toast.LENGTH_SHORT).show()
        }

    }

    private fun addNewStudent() {

        val firstName = binding.edtFirstName.text.toString()
        val lastName = binding.edtLastName.text.toString()
        val score = binding.edtScore.text.toString()
        val course = binding.edtCourse.text.toString()

        if (
            firstName.isNotEmpty() &&
            lastName.isNotEmpty() &&
            course.isNotEmpty() &&
            score.isNotEmpty()
        ) {





            val jsonObject = JsonObject()
            jsonObject.addProperty("name", firstName + " " + lastName)
            jsonObject.addProperty("course", course)
            jsonObject.addProperty("score", score.toInt())





           /* apiService.insertStudent(jsonObject).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {

                }

                override fun onFailure(call: Call<String>, t: Throwable) {}
            })*/

            /*            USING RXJAVA              */


            apiService.insertStudent(jsonObject)
                .asyncRequest()
                .subscribe(object : CompletableObserver {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onComplete() {
                        showToast("student inserted :)")
                        onBackPressed()
                    }

                    override fun onError(e: Throwable) {
                        showToast("error -> " + e.message ?: "null")
                    }

                })
            

            /*Toast.makeText(this, "insert finished!", Toast.LENGTH_SHORT).show()
            onBackPressed()*/

        } else {
            Toast.makeText(this, "لطفا اطلاعات را کامل وارد کنید", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }

        return true
    }

}