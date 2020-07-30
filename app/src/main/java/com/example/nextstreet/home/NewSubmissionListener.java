package com.example.nextstreet.home;

import com.example.nextstreet.models.PackageRequest;

public interface NewSubmissionListener {
  void respondToNewSubmission(PackageRequest request);
}
