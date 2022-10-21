package com.helper.app.activities;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.helper.app.R;
import com.helper.app.adapters.CourseSummaryAdapter;
import com.helper.app.fragments.ProgressDialogFragment;
import com.helper.app.models.Course;
import com.helper.app.models.Lecture;
import com.helper.app.models.Summary;
import com.helper.app.presenters.LecturesCallback;
import com.helper.app.presenters.LecturesPresenter;
import com.helper.app.utils.Constants;
import com.helper.app.utils.LocaleHelper;

import java.util.ArrayList;
import java.util.List;

public class CourseSummaryActivity extends BaseActivity implements LecturesCallback {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView name;
    private TextView degreePerLecture;
    private TextView message;
    private ViewGroup containerTotal;
    private TextView total;
    private View separator;

    private LecturesPresenter presenter;
    private CourseSummaryAdapter adapter;
    private List<Lecture> lectures;
    private List<Summary> summaries;
    private Course course;
    private boolean isLecturerAccount;
    private String userId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.setLocale(this, getCurrentLanguage().getLanguage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_summary);

        toolbar = findViewById(R.id.toolbar);
        containerTotal = findViewById(R.id.container_total);
        total = findViewById(R.id.total);
        separator = findViewById(R.id.separator);
        setupSupportedActionBar(toolbar);
        setActionBarTitle(getString(R.string.str_course_summary_title));

        name = findViewById(R.id.name);
        degreePerLecture = findViewById(R.id.degree_per_lecture);

        course = getIntent().getParcelableExtra(Constants.ARG_OBJECT);
        presenter = new LecturesPresenter(this);
        message = findViewById(R.id.message);
        isLecturerAccount = true;
        summaries = new ArrayList<>();
        lectures = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CourseSummaryAdapter(summaries, !isLecturerAccount);
        recyclerView.setAdapter(adapter);
        bindCourse();
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        presenter.getLecturesByCourseId(course.getId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }

    protected void setupSupportedActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolBarShadowStyle);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    protected void setActionBarTitle(int titleId) {
        getSupportActionBar().setTitle(titleId);
    }

    protected void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onShowLoading() {
        ProgressDialogFragment.show(getSupportFragmentManager());
    }

    @Override
    public void onHideLoading() {
        ProgressDialogFragment.hide(getSupportFragmentManager());
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void bindCourse() {
        if (course.getStudents() == null) {
            course.setStudents(new ArrayList<>());
        }
        name.setText(course.getName());
        degreePerLecture.setText(getString(R.string.str_degree_per_lecture_summary, course.getDegreePerLecture() + ""));
    }

    @Override
    public void onGetLecturesComplete(List<Lecture> lectures) {
        this.lectures.clear();
        this.lectures.addAll(lectures);
        summaries.clear();

        float total = 0;
        for (Lecture lecture : lectures) {
            if (lecture.getStudents() == null) {
                lecture.setStudents(new ArrayList<>());
            }
            Summary summary = new Summary(lecture.getId(), lecture.getName());
            if (isLecturerAccount) {
                summary.setStudentNumber(lecture.getStudents().size());
            } else {
                if (lecture.getStudents().contains(userId)) {
                    summary.setDegree(course.getDegreePerLecture());
                    total += course.getDegreePerLecture();
                }
            }
            summaries.add(summary);
        }

        if (!isLecturerAccount) {
            this.containerTotal.setVisibility(View.VISIBLE);
            this.separator.setVisibility(View.VISIBLE);
            this.total.setText(total + "");
        } else {
            this.containerTotal.setVisibility(View.INVISIBLE);
            this.separator.setVisibility(View.INVISIBLE);
        }

        message.setVisibility(this.lectures.isEmpty() ? View.VISIBLE : View.GONE);
        summaries.add(0, new Summary("", ""));
        adapter.notifyDataSetChanged();
    }
}