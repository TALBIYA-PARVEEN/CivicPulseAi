//package com.talbiya.CivicPulseAi.service;
//
//import com.talbiya.CivicPulseAi.entity.Issue;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class DuplicateDetectionService {
//
//    public Issue findDuplicate(
//            String title,
//            String description,
//            List<Issue> existingIssues) {
//
//        return null;
//    }
//}

package com.talbiya.CivicPulseAi.service;

import com.talbiya.CivicPulseAi.entity.Issue;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DuplicateDetectionService {

    public Issue findDuplicate(
            String title,
            String description,
            List<Issue> existingIssues) {

        String newText =
                (title + " " + description).toLowerCase();

        for (Issue issue : existingIssues) {

            String oldText =
                    (issue.getTitle() + " "
                            + issue.getDescription())
                            .toLowerCase();

            if (newText.contains(issue.getTitle().toLowerCase())
                    || oldText.contains(title.toLowerCase())) {

                return issue;
            }
        }

        return null;
    }
}