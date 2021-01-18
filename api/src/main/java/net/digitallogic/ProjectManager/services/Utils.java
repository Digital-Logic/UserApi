package net.digitallogic.ProjectManager.services;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
	public static List<Order> processSortBy(String str) {
		return Arrays.stream(str.split("\\s*,\\s*"))
				// transform property to cameCase if it is in snake_case.
				.map(Utils::snakeCaseToCamel)
				.map(property -> {
					if (property.charAt(0) == '-')
						return new Order(Sort.Direction.DESC, property.substring(1));
					else
						return new Order(Sort.Direction.ASC, property);
				})
				.collect(Collectors.toList());
	}

	public static List<String> snakeCaseToCamel(String[] strs) {
		return snakeCaseToCamel(List.of(strs));
	}
	public static List<String> snakeCaseToCamel(List<String> strs) {
		return strs.stream()
				.map(Utils::snakeCaseToCamel)
				.collect(Collectors.toList());
	}

	public static String snakeCaseToCamel(String str) {
		String[] split = str.split("_");
		StringBuilder sb = new StringBuilder(split[0]);

		for (int i = 1; i < split.length; ++i) {
			sb.append(split[i].substring(0, 1).toUpperCase())
				.append(split[i].substring(1).toLowerCase());
		}
		return sb.toString();
	}


	public static String toString(String[] arr) {
		StringBuilder sb = new StringBuilder();
		for (String str : arr) {
			sb.append(str)
					.append(" ");
		}
		return sb.substring(0, sb.length() - 1);
	}
}
