package com.ikki.immigrant.domain.subject;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.ikki.immigrant.domain.subject.entity.Subject;
import com.ikki.immigrant.domain.subject.valueobj.SubjectType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Optional;

@Service
@Slf4j
public class SubjectService {

    private static final String EMAIL_IDENTIFIER = "@";
    private static final String PHONE_IDENTIFIER = "+";
    private static final String EMPTY_STR = "";

    private static final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public Optional<Subject> signup(String subject) {
        Optional<Subject> sbj = checkExist(subject);
        if (!sbj.isPresent()) {
            Subject signupObj = new Subject();
            signupObj.setEmail(EMPTY_STR);
            signupObj.setUsername(EMPTY_STR);
            signupObj.setPhone(EMPTY_STR);
            signupObj.setAvailable(Subject.AvailableStatus.NORMAL);
            signupObj.setValid(EnumSet.noneOf(Subject.ValidStatus.class));

            SubjectType subjectType = checkType(subject);
            switch (subjectType) {
                case EMAIL:
                    signupObj.setEmail(subject);
                    break;
                case PHONE:
                    signupObj.setPhone(subject);
                    break;
                case USER_NAME:
                    signupObj.setUsername(subject);
                    break;
                default:
                    return Optional.empty();
            }
            subjectRepository.save(signupObj);
            return Optional.of(signupObj);
        }
        return Optional.empty();
    }

    public Optional<Subject> checkExist(String subject) {
        SubjectType subjectType = checkType(subject);
        switch (subjectType) {
            case EMAIL:
                return subjectRepository.findByEmail(subject);
            case PHONE:
                return subjectRepository.findByPhone(subject);
            case USER_NAME:
                return subjectRepository.findByUsername(subject);
            default:
                return Optional.empty();
        }
    }

    public SubjectType checkType(String subject) {
        if (subject.contains(EMAIL_IDENTIFIER)) {
            return SubjectType.EMAIL;
        }

        if (subject.indexOf(PHONE_IDENTIFIER) == 0) {
            try {
                Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(subject, "ZZ");
                if (phoneUtil.isValidNumber(phoneNumber)) {
                    return SubjectType.PHONE;
                }
            } catch (NumberParseException e) {
                log.warn("subject: [{}] may not a phone number: {}", subject, e.getMessage());
                return SubjectType.UNKNOWN;
            }
        }

        return SubjectType.USER_NAME;
    }
}
