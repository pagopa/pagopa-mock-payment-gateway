package it.gov.pagopa.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table(name = "client")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TableClient {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "auth_key")
    private String authKey;

    @Column(name = "base_url")
    private String baseUrl;

}
