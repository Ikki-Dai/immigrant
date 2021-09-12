package com.ikki.immigrant.domain.subject;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Optional;

@Service
@Slf4j
public class SubjectService {

    private static final String EMAIL_IDENTIFIER = "@";
    private static final String PHONE_IDENTIFIER = "+";
    private static final String empty_str = "";
    private final static PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public Optional<Subject> signup(String subject) {
        Optional<Subject> sbj = checkExist(subject);
        if (!sbj.isPresent()) {
            Subject signupObj = new Subject();
            signupObj.setAvailable(Subject.AvailableStatus.NORMAL);
            signupObj.setValid(EnumSet.noneOf(Subject.ValidStatus.class));
            if (subject.indexOf(EMAIL_IDENTIFIER) > 0) {
                signupObj.setEmail(subject);
                signupObj.setUsername(empty_str);
                signupObj.setPhone(empty_str);
            } else if (subject.indexOf(PHONE_IDENTIFIER) == 0) {
                try {
                    Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(subject, "ZZ");
                    if (phoneUtil.isValidNumber(phoneNumber)) {
                        signupObj.setEmail(empty_str);
                        signupObj.setUsername(empty_str);
                        signupObj.setPhone(subject);
                    }
                } catch (NumberParseException e) {
                    log.warn("subject: [{}] may not a phone number: {}", subject, e.getMessage());
                    return Optional.empty();
                }
            } else {
                signupObj.setEmail(empty_str);
                signupObj.setUsername(subject);
                signupObj.setPhone(empty_str);
            }
            subjectRepository.save(signupObj);
            return Optional.of(signupObj);
        }
        return Optional.empty();
    }

    public Optional<Subject> checkExist(String subject) {
        Optional<Subject> sbj = Optional.empty();
        if (subject.indexOf(EMAIL_IDENTIFIER) > 0) {
            return subjectRepository.findByEmail(subject);
        }
        if (subject.indexOf(PHONE_IDENTIFIER) == 0) {
            try {
                Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(subject, "ZZ");
                if (phoneUtil.isValidNumber(phoneNumber)) {
                    return subjectRepository.findByPhone(subject);
                }
            } catch (NumberParseException e) {
                log.warn("subject: [{}] may not a phone number: {}", subject, e.getMessage());
            }
        } else {
            sbj = subjectRepository.findByUsername(subject);
        }
        return sbj;
    }


}
