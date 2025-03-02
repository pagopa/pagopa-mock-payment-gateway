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
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "base_url")
    private String baseUrl;

}
