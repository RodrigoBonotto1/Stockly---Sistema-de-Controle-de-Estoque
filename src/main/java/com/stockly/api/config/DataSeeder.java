package com.stockly.api.config;

import com.stockly.api.entity.Product;
import com.stockly.api.entity.Role;
import com.stockly.api.entity.User;
import com.stockly.api.repository.ProductRepository;
import com.stockly.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsers();
        seedProducts();
    }

    private void seedUsers() {
        if (userRepository.count() > 0) {
            return;
        }

        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .build();

        userRepository.save(admin);
        log.info("Usuário padrão criado -> username: admin | password: admin123");
    }

    private void seedProducts() {
        if (productRepository.count() > 0) {
            return;
        }

        List<Product> products = List.of(
                build("Mouse Gamer RGB", "MOU-001", "Mouse óptico com iluminação RGB e 6 botões", "Periféricos", 45, "89.90"),
                build("Teclado Mecânico", "TEC-002", "Teclado mecânico switch blue com abnt2", "Periféricos", 30, "249.90"),
                build("Monitor 24'' Full HD", "MON-003", "Monitor LED IPS 24 polegadas 75Hz", "Monitores", 15, "699.00"),
                build("Notebook 15 polegadas", "NOT-004", "Notebook Intel i5, 8GB RAM, SSD 256GB", "Notebooks", 8, "3299.00"),
                build("SSD NVMe 512GB", "SSD-005", "SSD NVMe M.2 512GB leitura 3500MB/s", "Armazenamento", 60, "289.90"),
                build("Memória RAM 8GB DDR4", "RAM-006", "Módulo de memória DDR4 8GB 3200MHz", "Componentes", 80, "129.90"),
                build("Fone de Ouvido Headset", "FON-007", "Headset com microfone e som surround 7.1", "Periféricos", 40, "159.90"),
                build("Webcam Full HD", "WEB-008", "Webcam 1080p com microfone embutido", "Periféricos", 25, "179.90"),
                build("Cadeira Gamer", "CAD-009", "Cadeira ergonômica reclinável para escritório", "Móveis", 10, "899.00"),
                build("Placa de Vídeo 8GB", "GPU-010", "Placa de vídeo dedicada 8GB GDDR6", "Componentes", 5, "1899.00"),
                build("Roteador Wi-Fi 6", "ROT-011", "Roteador dual band Wi-Fi 6 alta velocidade", "Redes", 20, "349.90"),
                build("HD Externo 1TB", "HDX-012", "HD externo portátil USB 3.0 1TB", "Armazenamento", 35, "259.90")
        );

        productRepository.saveAll(products);
        log.info("{} produtos cadastrados via seed", products.size());
    }

    private Product build(String name, String sku, String description, String category, int qty, String price) {
        return Product.builder()
                .name(name)
                .sku(sku)
                .description(description)
                .category(category)
                .quantity(qty)
                .price(new BigDecimal(price))
                .build();
    }
}
