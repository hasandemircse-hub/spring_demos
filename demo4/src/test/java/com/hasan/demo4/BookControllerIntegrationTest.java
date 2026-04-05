package com.hasan.demo4;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerIntegrationTest {
/*
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void getAll_shouldReturn200() throws Exception {
        repository.save(new Book(null, "Suç ve Ceza", "Dostoyevski"));

        mockMvc.perform(get("/api/books"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Suç ve Ceza"));
    }

    @Test
    void create_shouldReturn201_whenValid() throws Exception {
        Book book = new Book(null, "Yeni Kitap", "Yazar");

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.title").value("Yeni Kitap"));
    }

    @Test
    void create_shouldReturn400_whenTitleBlank() throws Exception {
        Book book = new Book(null, "", "Yazar");

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").exists());
    }

    @Test
    void getById_shouldReturn404_whenNotFound() throws Exception {
        mockMvc.perform(get("/api/books/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldReturn200_whenExists() throws Exception {
        Book saved = repository.save(new Book(null, "Silinecek", "Yazar"));

        mockMvc.perform(delete("/api/books/" + saved.getId()))
            .andExpect(status().isOk());
    }
             */
}