package com.LibraryManagement.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Setter
@Getter
@Builder
public class Book {

	@Id
	private String bookId;

	private String title;
	private String author;
	private String genre;
	private int availableCopies;

	@Enumerated(EnumType.STRING)
	private BookStatus status;
//	private LocalDate dueDate;

	public enum BookStatus {
		AVAILABLE, BORROWED, OVERDUE
	}

	 


}
