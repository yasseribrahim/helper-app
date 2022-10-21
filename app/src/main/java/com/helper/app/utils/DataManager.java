package com.helper.app.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.helper.app.R;
import com.helper.app.models.About;
import com.helper.app.models.Grade;
import com.helper.app.models.User;
import com.helper.app.models.UserType;
import com.helper.app.presenters.UsersCallback;
import com.helper.app.presenters.UsersPresenter;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    public static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static final DatabaseReference NODE_USERS = database.getReference(Constants.NODE_NAME_USERS);

    public static void initUserAdmin() {
        User user = new User();
        user.setId("PWLxoxf5D6NdAUPdIifTcwx5JLh2");
        user.setUsername("admin@helper.com");
        NODE_USERS.child("PWLxoxf5D6NdAUPdIifTcwx5JLh2").setValue(user);
    }

    public static void initAbout() {
        About aboutEn = new About();
        About aboutAr = new About();

        aboutEn.setContent("The application displays the university student card electronically and activates the QR code feature, whereby the university can dispense with printed cards and achieve the goals of electronic transformation. And this makes it Easy for extracting the card and not having to print it, as the card is electronic. It is safe to use, as when the plastic card is lost, it can be misused. As for the electronic card, we just change the student’s password.");
        aboutEn.setConditions("The student must have a mobile device and also have the software installed on his device. He must be registered with the university and agree to the terms of use of the application");
        aboutEn.setObjectives("Students and Doctors at the university Lecturers and employees. Retrieval of the students data process becomes easy. The lecturer can manage students in the lecture easily. helping the lecturers take the absence of students more easily. In this way and the process of updating data will be more easily.");
        aboutAr.setContent("يقوم التطبيق بعرض بطاقة الطالب الجامعي إلكترونياً وتفعيل خاصية QR code حيث يمكن للجامعة الاستغناء عن البطاقات المطبوعة وتحقيق أهداف التحول الإلكتروني. وهذا يسهل إخراج البطاقة وعدم الاضطرار إلى طباعتها ، حيث أن البطاقة إلكترونية. إنه آمن للاستخدام ، حيث أنه عند فقدان البطاقة البلاستيكية ، يمكن إساءة استخدامها. بالنسبة للبطاقة الإلكترونية ، نقوم فقط بتغيير كلمة مرور الطالب");
        aboutAr.setConditions("يجب أن يكون لدى الطالب جهاز محمول وأن يكون البرنامج مثبتًا على جهازه أيضًا. يجب أن يكون مسجلاً في الجامعة ويوافق على شروط استخدام التطبيق.");
        aboutAr.setObjectives("الطلاب والأطباء في الجامعة المحاضرون والموظفون. يصبح استرداد عملية بيانات الطالب أمرًا سهلاً. يستطيع المحاضر إدارة الطلاب في المحاضرة بسهولة. مساعدة المحاضرين على استيعاب غياب الطلاب بسهولة أكبر. بهذه الطريقة ستكون عملية تحديث البيانات أكثر سهولة.");

        database.getReference(Constants.NODE_NAME_ABOUT).child("en").setValue(aboutEn);
        database.getReference(Constants.NODE_NAME_ABOUT).child("ar").setValue(aboutAr);
    }
}
