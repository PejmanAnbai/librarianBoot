package com.gcit.lms.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.gcit.lms.dao.AuthorDAO;
import com.gcit.lms.dao.BookCopiesDAO;
import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.BookLoansDAO;
import com.gcit.lms.dao.BorrowerDAO;
import com.gcit.lms.dao.GenreDAO;
import com.gcit.lms.dao.LibraryBranchDAO;
import com.gcit.lms.dao.PublisherDAO;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.BookCopies;
import com.gcit.lms.entity.LibraryBranch;

@SuppressWarnings({ "rawtypes", "unchecked" })
@RestController
public class LibrarianService {

	@Autowired
	BookDAO bdao;
	@Autowired
	LibraryBranchDAO ldao;
	@Autowired
	PublisherDAO pdao;
	@Autowired
	BookCopiesDAO bcdao;
	@Autowired
	BookLoansDAO bldao;
	@Autowired
	AuthorDAO adao;
	@Autowired
	GenreDAO gdao;
	@Autowired
	BorrowerDAO brdao;
	
	List<HttpMessageConverter<?>> mc = new ArrayList<HttpMessageConverter<?>>();
	RestTemplate rest = new RestTemplate();
	String adminURL = "http://localhost:1111";
	String librarianURL = "http://localhost:2222";
	String borrowerURL = "http://localhost:3333";
	String URL = "";

	public LibrarianService() {
		rest = new RestTemplate();
		mc.add(new FormHttpMessageConverter());
		mc.add(new StringHttpMessageConverter());
		mc.add(new MappingJackson2HttpMessageConverter());
		rest.setMessageConverters(mc);
	}

	@Transactional
	@RequestMapping(value = "/Librarian/Branches", method = RequestMethod.GET, produces = { "application/json",
			"application/xml" })
	public List<LibraryBranch> readAllBranches() throws SQLException {

		return ldao.readAllBranches();
	}

	@Transactional
	@RequestMapping(value = "/Librarian/Branches/Branch", method = RequestMethod.POST, produces = { "application/json",
			"application/xml" })
	public void updateLibraryBranch(@RequestBody LibraryBranch libraryBranch) throws SQLException {
		ldao.updateLibraryBranch(libraryBranch);
	}
	@Transactional
	@RequestMapping(value = "/Librarian/Branches/{id}/bookCount", method = RequestMethod.GET, produces = {
			"application/json", "application/xml" })
	public Integer getBookCount(@PathVariable Integer id) throws SQLException {
		return bdao.getBooksCount(id);
	}
	
	@Transactional
	@RequestMapping(value = "/Librarian/Branches/{id}/Books", method = RequestMethod.GET, produces = {
			"application/json", "application/xml" })
	public List<Book> readBranchBooks(@PathVariable Integer id, @RequestParam Integer pageNo, HttpServletRequest req)
			throws SQLException {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(adminURL +  "/Admin/Branches/"+ id +"/Books")
				.queryParam("pageNo", pageNo);
		System.out.println(builder.toUriString());
		System.out.println(req.getRequestURI());
		ResponseEntity<List> st = rest.getForEntity(builder.toUriString(), List.class);
		List<Book> obj = st.getBody();
		return obj;
	}

	@Transactional
	@RequestMapping(value = "/Librarian/Branches/{branchId}/Books/{bookId}/BookCopies", method = RequestMethod.GET, produces = {
			"application/json", "application/xml" })
	public BookCopies readBookCopies(@PathVariable Integer branchId, @PathVariable Integer bookId) throws SQLException {
		return bcdao.readBookCopies(bookId, branchId);
	}

	@Transactional
	@RequestMapping(value = "/Librarian/Books/bookId/{id}", method = RequestMethod.GET, produces = { "application/json",
			"application/xml" })
	public Book readBookByPk(@PathVariable Integer id) throws SQLException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", "" + id);
		ResponseEntity<Book> st = rest.getForEntity(adminURL + "/Admin/Books/bookId/{id}", Book.class, params);
		Book obj = st.getBody();
		return obj;
	}

	@RequestMapping(value = "/Librarian/Branches/{branchId}/Books/{bookId}/NoOfCopies", method = RequestMethod.POST, produces = {
			"application/json", "application/xml" })
	public void updateNoOfCopies(@PathVariable Integer branchId, @PathVariable Integer bookId,
			@RequestBody BookCopies bookCopies) throws SQLException {
		bcdao.updateBookCopies(bookId, branchId, bookCopies.getNoOfCopies());
	}
	
	@Transactional
	@RequestMapping(value = {"/Librarian/Branches/Name"}, method = RequestMethod.GET, produces = {
					"application/json", "application/xml" })
	public List<Object> readBranches(@RequestParam(value = "searchString", required = false) String searchString,
			@RequestParam(value = "pageNo", required = false) Integer pageNo)
			throws SQLException {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(adminURL + "/Admin/Branches/Name")
				.queryParam("searchString", searchString).queryParam("pageNo", pageNo);
		ResponseEntity<List> st = rest.getForEntity(builder.toUriString(), List.class);
		List<Object> obj = st.getBody();
		return obj;
	}

	@Transactional
	@RequestMapping(value = "/Librarian/Branches/branchId/{id}", method = RequestMethod.GET, produces = {
			"application/json", "application/xml" })
	public LibraryBranch readBranchByPK(@PathVariable Integer id) throws SQLException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", "" + id);
		ResponseEntity<LibraryBranch> st = rest.getForEntity(adminURL + "/Admin/Branches/branchId/{id}", LibraryBranch.class, params);
		LibraryBranch obj = st.getBody();
		return obj;
	}

	@Transactional
	@RequestMapping(value = "/Librarian/branchesCount", method = RequestMethod.GET, produces = { "application/json",
			"application/xml" })
	public Integer getBranchCount(HttpServletRequest req) throws SQLException {
		ResponseEntity<Integer> st = rest.getForEntity(adminURL + "/Admin/branchesCount", Integer.class);
		Integer count = st.getBody();
		return count;
	}
}
