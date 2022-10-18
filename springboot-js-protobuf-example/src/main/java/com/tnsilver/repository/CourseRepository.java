package com.tnsilver.repository;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Repository;
import org.springframework.web.context.annotation.SessionScope;

import com.tnsilver.protobuf.TrainingProtos.Course;
import com.tnsilver.protobuf.TrainingProtos.Courses;
import com.tnsilver.protobuf.TrainingProtos.PhoneNumbers;
import com.tnsilver.protobuf.TrainingProtos.Student;
import com.tnsilver.protobuf.TrainingProtos.Student.PhoneNumber;
import com.tnsilver.protobuf.TrainingProtos.Student.PhoneType;
import com.tnsilver.protobuf.TrainingProtos.Students;

import lombok.extern.slf4j.Slf4j;

@Repository
//@Scope(value=WebApplicationContext.SCOPE_SESSION,proxyMode = ScopedProxyMode.TARGET_CLASS)
@SessionScope
@Slf4j
public class CourseRepository {

	private AtomicInteger courseSeq = new AtomicInteger(0);
	private AtomicInteger studentSeq = new AtomicInteger(1000);
	private AtomicInteger phoneSeq = new AtomicInteger(100);

	Map<Integer, Course> courses;
	private ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock();
	private ReadLock readLock = rwlock.readLock();
	private WriteLock writeLock = rwlock.writeLock();

	@PostConstruct
	public void init() {
		courseSeq.set(0);
		studentSeq.set(1000);
		phoneSeq.set(100);
		courses = new LinkedHashMap<>();
		// @formatter:off
		Course course0 = Course.newBuilder()
				.setId(courseSeq.addAndGet(1))
				.setCourseName("Dummy Course")
				.addAllStudent(List.of(Student.newBuilder()
											  .setId(1000)
											  .setFirstName("Test")
											  .setLastName("Tester")
											  .setEmail("tester@testers.com")
											  .addAllCourseRefs(List.of(courseSeq.get()))
											  .build()))
				.build();
		Course course1 = Course.newBuilder()
				.setId(courseSeq.addAndGet(1))
				.setCourseName("Protobuf with SpringBoot")
				.addAllStudent(createTestStudents(true,courseSeq.get())).build();
		Course course2 = Course.newBuilder()
				.setId(courseSeq.addAndGet(1))
				.setCourseName("REST with SpringBoot")
				.addAllStudent(createTestStudents(false,courseSeq.get())).build();
		// @formatter:on
		courses.put(course0.getId(), course0);
		courses.put(course1.getId(), course1);
		courses.put(course2.getId(), course2);
	}

	public Course findCourseById(int id) {
		readLock.lock();
		try {
			return courses.get(id);
		} finally {
			readLock.unlock();
		}
	}

	public Student findStudentById(int id) {
		readLock.lock();
		try {
			Course course = courses.values().stream()
					.filter(c -> c.getStudentList().stream().anyMatch(s -> s.getId() == id)).findAny().orElse(null);
			if (null != course) {
				Student student = course.getStudentList().stream().filter(s -> s.getId() == id).findAny().orElse(null);
				return student;
			}
			log.warn("cannot find student id {}", id);
			return null;
		} finally {
			readLock.unlock();
		}
	}

	public Courses findAll() {
		readLock.lock();
		try {
			return Courses.newBuilder().addAllCourses(courses.values()).build();
		} finally {
			readLock.unlock();
		}
	}

	public Course save(Course course) {
		writeLock.lock();
		try {
			if (course.getId() == 0) {
				int newId = courses.keySet().stream().max((a, b) -> a.compareTo(b)).orElse(0);
				course = course.toBuilder().setId(newId + 1).build();
			}
			courses.put(course.getId(), course);
			return course;
		} finally {
			writeLock.unlock();
		}
	}

	public Student save(Student student) {
		Course course = findCourseById(student.getCourseRefs(0));
		writeLock.lock();
		// @formatter:off
		try {
			if (null != course) {
				List<Student> students = new LinkedList<>();
				int newId = student.getId();
				if (newId == 0) { // assign max id;
					for (Course c : courses.values()) {
						for (Student s : c.getStudentList()) {
							newId = s.getId() > newId ? s.getId() : newId;
						}
					}
					student = Student.newBuilder(student).setId(newId + 1).build();
				}
				students.add(student);
				final int studentId = student.getId();
				course.getStudentList().stream().filter(s -> s.getId() != studentId).forEach(students::add);
				course = Course.newBuilder(course).clearStudent().addAllStudent(students).build();
				courses.put(course.getId(), course);
				return student;
			} else {
				log.warn("cannot save student {}", student);
				return null;
			}
			// @formatter:on
		} finally {
			writeLock.unlock();
		}
	}

	public PhoneNumber save(PhoneNumber phoneToSave) {
		writeLock.lock();
		// @formatter:off
		try {
			int studentRefId = phoneToSave.getStudentRef();
			Course course = null;
			Student student = null;
			PhoneNumber phoneNumber = null;
			boolean found = false;
			Iterator<Course> citer = courses.values().iterator();
			while (citer.hasNext() && !found) {
				Course c = citer.next();
				Iterator<Student> siter = c.getStudentList().iterator();
				while (siter.hasNext() && !found) {
					Student s = siter.next();
					if (s.getId() == studentRefId) {
						course = c;
						student = s;
						if (phoneToSave.getId() != 0) {
							Iterator<PhoneNumber> piter = s.getPhoneList().iterator();
							while (piter.hasNext() && !found) {
								PhoneNumber p = piter.next();
								if (p.getId() == phoneToSave.getId()) {
									phoneNumber = p;
									found = true;
								}
							}
						}
					}
				}
			}
			if (null != course && null != student) {
				List<PhoneNumber> phoneNumbers = new LinkedList<>();
				int newId = phoneToSave.getId();
				if (newId == 0) {
					for (Course c : courses.values())
						for (Student s : c.getStudentList())
							for (PhoneNumber p : s.getPhoneList())
								newId = p.getId() > newId ? p.getId() : newId;
					newId++;
				}
				phoneNumber = PhoneNumber.newBuilder(phoneToSave).setId(newId).build();
				phoneNumbers.add(phoneNumber);
				int id = phoneNumber.getId();
				student.getPhoneList().stream().filter(pn -> pn.getId() != id).forEach(phoneNumbers::add);
				student = Student.newBuilder(student).clearPhone().addAllPhone(phoneNumbers).build();
				List<Student> students = new LinkedList<>();
				students.add(student);
				course.getStudentList().stream().filter(s -> s.getId() != studentRefId).forEach(students::add);
				course = Course.newBuilder(course).clearStudent().addAllStudent(students).build();
				courses.put(course.getId(), course);
				return phoneNumber;
			} else {
				log.warn("cannot save {}", phoneToSave);
			}
			return null;
			// @formatter:on
		} finally {
			writeLock.unlock();
		}
	}

	public boolean delete(Course course) {
		writeLock.lock();
		try {
			return deleteCourseById(course.getId());
		} finally {
			writeLock.unlock();
		}
	}

	public boolean deleteCourseById(Integer id) {
		writeLock.lock();
		try {
			if (courses.containsKey(id)) {
				boolean result = null != courses.remove(id);
				return result;
			}
			log.warn("could not delete course id {}", id);
			return false;
		} finally {
			writeLock.unlock();
		}
	}

	public boolean deleteStudentById(int id) {
		// @formatter:off
		writeLock.lock();
		try {
			Course course = courses.entrySet().stream()
					.map(Entry::getValue)
					.filter(c -> c.getStudentList().stream().anyMatch(s -> s.getId() == id))
					.findAny()
					.orElse(null);
			if (null != course && course.getStudentList().stream().anyMatch(s -> s.getId() == id)) {
				List<Student> students = new LinkedList<>();
				course.getStudentList().stream().filter(s -> s.getId() != id).forEach(students::add);
				course = Course.newBuilder(course).clearStudent().addAllStudent(students).build();
				courses.put(course.getId(), course);
				return true;
			}
			log.warn("student id {} does not exist", id);
			return false;
		} finally {
			writeLock.unlock();
		}
		// @formatter:on
	}

	public boolean deletePhoneById(int id) {
		// @formatter:off
		writeLock.lock();
		try {
			Course course = null;
			Student student = null;
			PhoneNumber phoneNumber = null;
			boolean found = false;
			Iterator<Course> citer = courses.values().iterator();
			while (citer.hasNext() && !found) {
				Course cour = citer.next();
				Iterator<Student> siter = cour.getStudentList().iterator();
				while (siter.hasNext() && !found) {
					Student stud = siter.next();
					Iterator<PhoneNumber> piter = stud.getPhoneList().iterator();
					while (piter.hasNext() && !found) {
						PhoneNumber pn = piter.next();
						if (pn.getId() == id) {
							course = cour;
							student = stud;
							phoneNumber = pn;
							found = true;
						}
					}
				}
			}
			if (null != phoneNumber) {
				int studentId = student.getId();
				List<PhoneNumber> phoneNumbers = new LinkedList<>();
				student.getPhoneList().stream().filter(pn -> pn.getId() != id).forEach(phoneNumbers::add);
				student = Student.newBuilder(student).clearPhone().addAllPhone(phoneNumbers).build();
				List<Student> students = new LinkedList<>();
				students.add(student);
				course.getStudentList().stream().filter(s -> s.getId() != studentId).forEach(students::add);
				course = Course.newBuilder(course).clearStudent().addAllStudent(students).build();
				courses.put(course.getId(), course);
				return true;
			}
			log.debug("phone number id {} does not exist", id);
			return false;
		} finally {
			writeLock.unlock();
		}
		// @formatter:on
	}


	public int count() {
		readLock.lock();
		try {
			return courses.size();
		} finally {
			readLock.unlock();
		}
	}

	public Students findCourseStudents(int id) {
		readLock.lock();
		try {
			Course course = findCourseById(id);
			if (null != course)
				return Students.newBuilder().setCourseId(id).addAllStudents(course.getStudentList()).build();
			log.warn("cannot find students for course id {}", id);
			return null;
		} finally {
			readLock.unlock();
		}
	}

	public PhoneNumbers findStudentPhoneNumbers(Integer id) {
		// @formatter:off
		readLock.lock();
		try {
			List<PhoneNumber> phoneNumberList = new LinkedList<>();
			courses.values().forEach(c -> c.getStudentList().forEach(s -> { if (s.getId() == id)  s.getPhoneList().forEach(phoneNumberList::add);}));
			return PhoneNumbers.newBuilder().setId(id).addAllPhone(phoneNumberList).build();
		} finally {
			readLock.unlock();
		}
		// @formatter:on
	}


	private List<Student> createTestStudents(boolean simpsons, int courseId) {
		// @formatter:off
		Student student1,student2,student3,student4,student5;
		if (simpsons) {
			student1 = createStudent(courseId,studentSeq.addAndGet(101), "Homer", "Simpson", "homer.simpson@gmail.com", Arrays.asList(createPhone(phoneSeq.addAndGet(10), "123456", PhoneType.MOBILE, studentSeq.get())));
			student2 = createStudent(courseId,studentSeq.addAndGet(101), "Marge", "Simpson", "marge.simpson@gmail.com", Arrays.asList(createPhone(phoneSeq.addAndGet(10), "654321", PhoneType.LANDLINE, studentSeq.get())));
			student3 = createStudent(courseId,studentSeq.addAndGet(101), "Lisa", "Simpson", "lisa.simpson@gmail.com", Arrays.asList(createPhone(phoneSeq.addAndGet(10), "234567", PhoneType.MOBILE, studentSeq.get()),createPhone(phoneSeq.addAndGet(10), "345678", PhoneType.LANDLINE, studentSeq.get())));
			student4 = createStudent(courseId,studentSeq.addAndGet(101), "Bart", "Simpson", "bart.simpson@gmail.com", Arrays.asList(createPhone(phoneSeq.addAndGet(10), "456789", PhoneType.MOBILE, studentSeq.get()),createPhone(phoneSeq.addAndGet(10), "567890", PhoneType.LANDLINE, studentSeq.get())));
			student5 = createStudent(courseId,studentSeq.addAndGet(101), "Maggie", "Simpson", "maggie.simpson@gmail.com", Arrays.asList(createPhone(phoneSeq.addAndGet(10), "765432", PhoneType.MOBILE, studentSeq.get())));
		} else {
			student1 = createStudent(courseId,studentSeq.addAndGet(101), "John", "Doe", "john.doe@gmail.com", Arrays.asList(createPhone(phoneSeq.addAndGet(10), "123456", PhoneType.MOBILE, studentSeq.get())));
			student2 = createStudent(courseId,studentSeq.addAndGet(101), "Richard", "Roe", "richard.roe@gmail.com", Arrays.asList(createPhone(phoneSeq.addAndGet(10), "234567", PhoneType.LANDLINE, studentSeq.get())));
			student3 = createStudent(courseId,studentSeq.addAndGet(101), "Jane", "Doe", "jane.doe@yahoo.com", Arrays.asList(createPhone(phoneSeq.addAndGet(10), "345678", PhoneType.MOBILE, studentSeq.get()), createPhone(phoneSeq.addAndGet(10), "456789", PhoneType.LANDLINE, studentSeq.get())));
			student4 = createStudent(courseId,studentSeq.addAndGet(101), "Dick", "Tracey", "dick.tracy@yahoo.com", Arrays.asList(createPhone(phoneSeq.addAndGet(10), "348678", PhoneType.MOBILE, studentSeq.get()), createPhone(phoneSeq.addAndGet(10), "678976", PhoneType.LANDLINE, studentSeq.get())));
			student5 = createStudent(courseId,studentSeq.addAndGet(101), "Joan", "Jett", "joan.jett@yahoo.com", Arrays.asList(createPhone(phoneSeq.addAndGet(10), "456789", PhoneType.MOBILE, studentSeq.get()), createPhone(phoneSeq.addAndGet(10), "878765", PhoneType.LANDLINE, studentSeq.get())));
		}
		// @formatter:on
		return List.of(student1, student2, student3, student4, student5);
	}

	private Student createStudent(int courseId, int id, String firstName, String lastName, String email,
			List<PhoneNumber> phones) {
		// @formatter:off
		return Student.newBuilder().setId(id)
				.setFirstName(firstName)
				.setLastName(lastName)
				.setEmail(email)
				.addAllPhone(phones)
				.addAllCourseRefs(List.of(courseId))
				.build();
		// @formatter:on
	}

	private PhoneNumber createPhone(Integer id, String number, PhoneType type, Integer studentRef) {
		return PhoneNumber.newBuilder().setId(id).setNumber(number).setType(type).setStudentRef(studentRef).build();
	}

}
