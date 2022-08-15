package ir.MrMohamadHosein.studentManager_Rxjava

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ir.MrMohamadHosein.studentManager_Rxjava.databinding.ActivityMainBinding
import ir.MrMohamadHosein.studentManager_Rxjava.net.ApiService
import ir.MrMohamadHosein.studentManager_Rxjava.recycler.Student
import ir.MrMohamadHosein.studentManager_Rxjava.recycler.StudentAdapter
import ir.MrMohamadHosein.studentManager_Rxjava.util.BASE_URL
import ir.MrMohamadHosein.studentManager_Rxjava.util.asyncRequest
import ir.MrMohamadHosein.studentManager_Rxjava.util.showToast
import retrofit2.*
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

// 1. dependency
// 2. call adapter factory
// 3. return type
// 4. subscribe
// 5. some edits


class MainActivity : AppCompatActivity(), StudentAdapter.StudentEvent {
    lateinit var binding: ActivityMainBinding
    lateinit var myAdapter: StudentAdapter
    lateinit var apiService: ApiService
    lateinit var disposable: Disposable

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMain)

        val retrofit = Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        binding.btnAddStudent.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }


    }

    override fun onResume() {
        super.onResume()

        getDataFromApi()
    }

    fun getDataFromApi() {

        apiService
            .getAllStudents()
            .asyncRequest()
            .subscribe( object :SingleObserver<List<Student>> {

                override fun onSubscribe(d: Disposable) {
                    disposable = d
                }

                override fun onSuccess(t: List<Student>) {
                    setDataToRecycler(t)
                }

                override fun onError(e: Throwable) {
                    Log.v("testLog" , e.message!!)
                }

            } )

    }

    fun setDataToRecycler(data: List<Student>) {
        val myData = ArrayList(data)
        myAdapter = StudentAdapter(myData, this)
        binding.recyclerMain.adapter = myAdapter
        binding.recyclerMain.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    override fun onItemClicked(student: Student, position: Int) {
        updateDataInServer(student, position)
    }

    override fun onItemLongClicked(student: Student, position: Int) {
        val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
        dialog.contentText = "Delete this Item?"
        dialog.cancelText = "cancel"
        dialog.confirmText = "confirm"
        dialog.setOnCancelListener {
            dialog.dismiss()
        }
        dialog.setConfirmClickListener {

            deleteDataFromServer(student, position)
            dialog.dismiss()

        }
        dialog.show()
    }

    private fun deleteDataFromServer(student: Student, position: Int) {

        /*apiService
            .deleteStudent(student.name)
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {

                }

                override fun onFailure(call: Call<String>, t: Throwable) {

                }
            })*/






        Log.v("deleteStudent studentid" , student.id.toString())


        apiService
            .deleteStudent(student.id)
            .subscribeOn( Schedulers.io() )
            .observeOn( AndroidSchedulers.mainThread() )
            .subscribe( object :CompletableObserver {


                override fun onSubscribe(d: Disposable) {
                    disposable = d
                }

                override fun onComplete() {
                    showToast("student deleted !")
                }


                override fun onError(e: Throwable) {
                    Log.v("deleteStudent not ok" , e.message!!)
                }

            } )


        myAdapter.removeItem(student, position)

    }

    private fun updateDataInServer(student: Student, position: Int) {

        val intent = Intent(this, MainActivity2::class.java)
        intent.putExtra("student", student)
        startActivity(intent)

    }


}